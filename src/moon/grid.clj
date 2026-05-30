(ns moon.grid
  (:require [clojure.math.circle :as circle]
            [clojure.math.rectangle :as rectangle]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.faction :as faction]
            [moon.grid2d :as g2d]
            [moon.position :as position])
  (:import (com.badlogic.gdx.math Circle
                                  Intersector
                                  Rectangle)))

(defn cells->entities [cells]
  (into #{} (mapcat :entities) cells))

; (g2d/get-cells grid (body/occupied-tiles body))
(defn- body->occupied-cells
  [grid
   {:keys [body/position
           body/width
           body/height]
    :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (g2d/get-cells grid (body/touched-tiles body))
    [(grid (mapv int position))]))

(defn circle->cells [g2d circle]
  (->> circle
       circle/outer-rectangle
       rectangle/touched-tiles
       (g2d/get-cells g2d)))

(defn circle->entities [g2d {:keys [position radius] :as circle}]
  (->> (circle->cells g2d circle)
       (map deref)
       cells->entities
       (filter #(Intersector/overlaps
                 (let [[x y] position]
                   (Circle. x y radius))
                 ^Rectangle (body/rectangle (:entity/body @%))))))

(defn cached-adjacent-cells [g2d cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (->> @cell
                      :position
                      position/get-8-neighbours
                      (g2d/get-cells g2d))]
      (swap! cell assoc :adjacent-cells result)
      result)))

(defn point->entities [g2d pos]
  (when-let [cell (g2d (mapv int pos))]
    (filter #(let [[x y] pos]
               (.contains ^Rectangle (body/rectangle (:entity/body @%)) x y))
            (:entities @cell))))

(defn set-touched-cells! [grid eid]
  (let [cells (g2d/get-cells grid (body/touched-tiles (:entity/body @eid)))]
    (assert (not-any? nil? cells))
    (swap! eid assoc ::touched-cells cells) ; TODO :entity/touched-cells ....
    (doseq [cell cells]
      (assert (not (get (:entities @cell) eid)))
      (swap! cell update :entities conj eid))))

(defn remove-from-touched-cells! [_ eid]
  (doseq [cell (::touched-cells @eid)]
    (assert (get (:entities @cell) eid))
    (swap! cell update :entities disj eid)))

(defn set-occupied-cells! [grid eid]
  (let [cells (body->occupied-cells grid (:entity/body @eid))]
    (doseq [cell cells]
      (assert (not (get (:occupied @cell) eid)))
      (swap! cell update :occupied conj eid))
    (swap! eid assoc ::occupied-cells cells)))

(defn remove-from-occupied-cells! [_ eid]
  (doseq [cell (::occupied-cells @eid)]
    (assert (get (:occupied @cell) eid))
    (swap! cell update :occupied disj eid)))

  ; TODO take entity ! some things not required @ body !?
(defn valid-position? [g2d {:keys [body/z-order] :as body} entity-id]
  (assert (:body/collides? body))
  (let [cells* (into [] (map deref) (g2d/get-cells g2d (body/touched-tiles body)))]
    (and (not-any? #(cell/blocked? % z-order) cells*)
         (->> cells*
              cells->entities
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:body/collides? (:entity/body other-entity))
                                 (body/overlaps? (:entity/body other-entity)
                                                 body)))))))))

(defn nearest-enemy-distance [grid entity]
  (cell/nearest-entity-distance @(grid (mapv int (:body/position (:entity/body entity))))
                                (faction/enemy (:entity/faction entity))))

(defn nearest-enemy [grid entity]
  (cell/nearest-entity @(grid (mapv int (:body/position (:entity/body entity))))
                       (faction/enemy (:entity/faction entity))))

; Assumption: The map contains no not-allowed diagonal cells, diagonal wall cells where both
; adjacent cells are walls and blocked.
; (important for wavefront-expansion and field-following)
; * entities do not move to NADs (they remove them)
; * the potential field flows into diagonals, so they should be reachable too.
;
; TODO assert somewhere/at map load no NAD's and @ potential field init & remove from
; potential-field-following the removal of NAD's.

; TODO remove max pot field movement player screen + 10 tiles as of screen size
; => is coupled to max-steps & also
; to friendly units follow player distance

(comment
 (clear-marked-cells! :good (get @faction->marked-cells :good))

 (defn- faction->tiles->entities-map* [entities]
   (into {}
         (for [[faction entities] (->> entities
                                       (filter   #(:entity/faction @%))
                                       (group-by #(:entity/faction @%)))]
           [faction
            (zipmap (map #(mapv int (:body/position (:entity/body @%))) entities)
                    entities)])))

 (def max-iterations 1)

 (let [entities (map ids->eids [140 110 91])
       tl->es (:good (faction->tiles->entities-map* entities))]
   tl->es
   (def last-marked-cells (generate-potential-field :good tl->es)))
 (println *1)
 (def marked *2)
 (step :good *1)
 )

; TODO performance
; * cached-adjacent-non-blocked-cells ? -> no need for cell blocked check?
; * sorted-set-by ?
; * do not refresh the potential-fields EVERY frame, maybe very 100ms & check for exists? target if they died inbetween.
; (or teleported?)
(defn- step [grid faction last-marked-cells]
  (let [marked-cells (transient [])
        distance       #(cell/nearest-entity-distance % faction)
        nearest-entity #(cell/nearest-entity          % faction)
        marked? faction]
    ; sorting important because of diagonal-cell values, flow from lower dist first for correct distance
    (doseq [cell (sort-by #(distance @%) last-marked-cells)
            adjacent-cell (cached-adjacent-cells grid cell)
            :let [cell* @cell
                  adjacent-cell* @adjacent-cell]
            :when (not (or (cell/pf-blocked? adjacent-cell*)
                           (marked? adjacent-cell*)))
            :let [distance-value (+ (float (distance cell*))
                                    (float (if (position/diagonal? (:position cell*)
                                                                   (:position adjacent-cell*))
                                             1.4 ; square root of 2 * 10
                                             1)))]]
      (swap! adjacent-cell cell/add-field-data faction distance-value (nearest-entity cell*))
      (conj! marked-cells adjacent-cell))
    (persistent! marked-cells)))

(defn- generate-potential-field
  [grid faction tiles->entities max-iterations]
  (let [entity-cell-seq (for [[tile eid] tiles->entities]
                          [eid (grid tile)])
        marked (map second entity-cell-seq)]
    (doseq [[eid cell] entity-cell-seq]
      (swap! cell cell/add-field-data faction 0 eid))
    (loop [marked-cells     marked
           new-marked-cells marked
           iterations 0]
      (if (= iterations max-iterations)
        marked-cells
        (let [new-marked (step grid faction new-marked-cells)]
          (recur (concat marked-cells new-marked)
                 new-marked
                 (inc iterations)))))))

(defn- tiles->entities [entities faction]
  (let [entities (filter #(= (:entity/faction @%) faction)
                         entities)]
    (zipmap (map #(mapv int (:body/position (:entity/body @%))) entities)
            entities)))

(defn tick! [grid pf-cache faction entities max-iterations]
  (let [tiles->entities (tiles->entities entities faction)
        last-state   [faction :tiles->entities]
        marked-cells [faction :marked-cells]]
    (when-not (= (get-in @pf-cache last-state) tiles->entities)
      (swap! pf-cache assoc-in last-state tiles->entities)
      (doseq [cell (get-in @pf-cache marked-cells)]
        (swap! cell cell/remove-field-data faction))
      (swap! pf-cache assoc-in marked-cells (generate-potential-field
                                             grid
                                             faction
                                             tiles->entities
                                             max-iterations)))))
