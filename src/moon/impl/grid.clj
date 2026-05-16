(ns moon.impl.grid
  (:require [clojure.math.circle :as circle]
            [clojure.math.rectangle :as rectangle]
            [clojure.math.vector2 :as v]
            [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.position :as position]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.faction :as faction]
            [moon.grid :as grid]
            [moon.grid2d :as g2d]
            [moon.cell :as cell])
  (:import (com.badlogic.gdx.math Circle
                                  Intersector
                                  Rectangle)))

(defn create [{:keys [ctx/tiled-map]}]
  (g2d/create-grid (props/get (tiled-map/properties tiled-map) "width")
                   (props/get (tiled-map/properties tiled-map) "height")
                   (fn [position]
                     (atom (cell/create position
                                        (case (tiled-map/movement-property tiled-map position)
                                          "none" :none
                                          "air"  :air
                                          "all"  :all))))))

(defn- body->occupied-cells
  [grid
   {:keys [body/position
           body/width
           body/height]
    :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (g2d/get-cells grid (body/touched-tiles body))
    [(grid (mapv int position))]))

(defn- indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d)) => ([0 a] [1 b] [2 c] [3 d])"
  [s]
  (map vector (iterate inc 0) s))

(defn- positions
  "Returns a lazy sequence containing the positions at which pred
  is true for items in coll."
  [pred coll]
  (for [[idx elt] (indexed coll) :when (pred elt)] idx))

(let [order (position/get-8-neighbours [0 0])]
  (def ^:private diagonal-check-indizes
    (into {} (for [[x y] (filter v/diagonal-direction? order)]
               [(first (positions #(= % [x y]) order))
                (vec (positions #(some #{%} [[x 0] [0 y]])
                                     order))]))))

(defn- is-not-allowed-diagonal? [at-idx adjacent-cells]
  (when-let [[a b] (get diagonal-check-indizes at-idx)]
    (and (nil? (adjacent-cells a))
         (nil? (adjacent-cells b)))))

(defn- remove-not-allowed-diagonals [adjacent-cells]
  (remove nil?
          (map-indexed
            (fn [idx cell]
              (when-not (or (nil? cell)
                            (is-not-allowed-diagonal? idx adjacent-cells))
                cell))
            adjacent-cells)))

; not using filter because nil cells considered @ remove-not-allowed-diagonals
; TODO only non-nil cells check
; TODO always called with cached-adjacent-cells ...
(defn- filter-viable-cells [eid adjacent-cells]
  (remove-not-allowed-diagonals
    (mapv #(when-not (or (cell/pf-blocked? @%)
                         (cell/occupied-by-other? @% eid))
             %)
          adjacent-cells)))

(defmacro ^:private when-seq [[aseq bind] & body]
  `(let [~aseq ~bind]
     (when (seq ~aseq)
       ~@body)))

(defn- get-min-dist-cell [distance-to cells]
  (when-seq [cells (filter distance-to cells)]
    (apply min-key distance-to cells)))

; rarely called -> no performance bottleneck
(defn- viable-cell? [grid distance-to own-dist eid cell]
  (when-let [best-cell (get-min-dist-cell
                        distance-to
                        (filter-viable-cells eid (grid/cached-adjacent-cells grid cell)))]
    (when (< (float (distance-to best-cell)) (float own-dist))
      cell)))

(defn- find-next-cell
  "returns {:target-entity eid} or {:target-cell cell}. Cell can be nil."
  [grid eid own-cell]
  (let [faction (faction/enemy (:entity/faction @eid))
        distance-to    #(cell/nearest-entity-distance @% faction)
        nearest-entity #(cell/nearest-entity          @% faction)
        own-dist (distance-to own-cell)
        adjacent-cells (grid/cached-adjacent-cells grid own-cell)]
    (if (and own-dist (zero? (float own-dist)))
      {:target-entity (nearest-entity own-cell)}
      (if-let [adjacent-cell (first (filter #(and (distance-to %)
                                                  (zero? (float (distance-to %))))
                                            adjacent-cells))]
        {:target-entity (nearest-entity adjacent-cell)}
        {:target-cell (let [cells (filter-viable-cells eid adjacent-cells)
                            min-key-cell (get-min-dist-cell distance-to cells)]
                        (cond
                         (not min-key-cell)  ; red
                         own-cell

                         (not own-dist)
                         min-key-cell

                         (> (float (distance-to min-key-cell)) (float own-dist)) ; red
                         own-cell

                         (< (float (distance-to min-key-cell)) (float own-dist)) ; green
                         min-key-cell

                         (= (distance-to min-key-cell) own-dist) ; yellow
                         (or
                          (some #(viable-cell? grid distance-to own-dist eid %) cells)
                          own-cell)))}))))

(defn- inside-cell? [grid entity cell]
  (let [cells (g2d/get-cells grid (body/touched-tiles (:entity/body entity)))]
    (and (= 1 (count cells))
         (= cell (first cells)))))

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
            adjacent-cell (grid/cached-adjacent-cells grid cell)
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

(extend-type moon.grid2d.VectorGrid
  grid/Grid
  (circle->cells [g2d circle]
    (->> circle
         circle/outer-rectangle
         rectangle/touched-tiles
         (g2d/get-cells g2d)))

  (circle->entities [g2d {:keys [position radius] :as circle}]
    (->> (grid/circle->cells g2d circle)
         (map deref)
         grid/cells->entities
         (filter #(Intersector/overlaps
                   ^Circle (let [[x y] position]
                             (Circle. x y radius))
                   ^Rectangle (body/rectangle (:entity/body @%))))))

  (cached-adjacent-cells [g2d cell]
    (if-let [result (:adjacent-cells @cell)]
      result
      (let [result (->> @cell
                        :position
                        position/get-8-neighbours
                        (g2d/get-cells g2d))]
        (swap! cell assoc :adjacent-cells result)
        result)))

  (point->entities [g2d [x y]]
    (when-let [cell (g2d (mapv int [x y]))]
      (filter #(Rectangle/.contains (body/rectangle (:entity/body @%)) x y)
              (:entities @cell))))

  (set-touched-cells! [grid eid]
    (let [cells (g2d/get-cells grid (body/touched-tiles (:entity/body @eid)))]
      (assert (not-any? nil? cells))
      (swap! eid assoc ::touched-cells cells) ; TODO :entity/touched-cells ....
      (doseq [cell cells]
        (assert (not (get (:entities @cell) eid)))
        (swap! cell update :entities conj eid))))

  (remove-from-touched-cells! [_ eid]
    (doseq [cell (::touched-cells @eid)]
      (assert (get (:entities @cell) eid))
      (swap! cell update :entities disj eid)))

  (set-occupied-cells! [grid eid]
    (let [cells (body->occupied-cells grid (:entity/body @eid))]
      (doseq [cell cells]
        (assert (not (get (:occupied @cell) eid)))
        (swap! cell update :occupied conj eid))
      (swap! eid assoc ::occupied-cells cells)))

  (remove-from-occupied-cells! [_ eid]
    (doseq [cell (::occupied-cells @eid)]
      (assert (get (:occupied @cell) eid))
      (swap! cell update :occupied disj eid)))

  ; TODO take entity ! some things not required @ body !?
  (valid-position? [g2d {:keys [body/z-order] :as body} entity-id]
    (assert (:body/collides? body))
    (let [cells* (into [] (map deref) (g2d/get-cells g2d (body/touched-tiles body)))]
      (and (not-any? #(cell/blocked? % z-order) cells*)
           (->> cells*
                grid/cells->entities
                (not-any? (fn [other-entity]
                            (let [other-entity @other-entity]
                              (and (not= (:entity/id other-entity) entity-id)
                                   (:body/collides? (:entity/body other-entity))
                                   (Rectangle/.overlaps ^Rectangle (body/rectangle (:entity/body other-entity))
                                                        ^Rectangle (body/rectangle body))))))))))

  (nearest-enemy-distance [grid entity]
    (cell/nearest-entity-distance @(grid (mapv int (:body/position (:entity/body entity))))
                                  (faction/enemy (:entity/faction entity))))

  (nearest-enemy [grid entity]
    (cell/nearest-entity @(grid (mapv int (:body/position (:entity/body entity))))
                         (faction/enemy (:entity/faction entity))))

  (find-direction [grid eid]
    (let [position (:body/position (:entity/body @eid))
          own-cell (grid (mapv int position))
          {:keys [target-entity target-cell]} (find-next-cell grid eid own-cell)]
      (cond
       target-entity
       (v/direction position (:body/position (:entity/body @target-entity)))

       (nil? target-cell)
       nil

       :else
       (when-not (and (= target-cell own-cell)
                      (cell/occupied-by-other? @own-cell eid)) ; prevent friction 2 move to center
         (when-not (inside-cell? grid @eid target-cell)
           (v/direction position (:middle @target-cell)))))))

  (tick! [grid pf-cache faction entities max-iterations]
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
                                               max-iterations))))))
