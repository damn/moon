(ns lib.grid
  (:require [clojure.math :as math]
            [lib.body :as body]
            [lib.cell :as cell]
            [lib.faction :as faction]
            [lib.position :as position]
            [lib.potential-field :as potential-field]
            [lib.v2 :as v2])
  (:import (com.badlogic.gdx.math Rectangle)))

(let [order (position/get-8-neighbours [0 0])
      diagonal? (fn [[^int x ^int y]]
                  (and (not (zero? x))
                       (not (zero? y))))]
  (def ^:private diagonal-check-indizes
    (into {} (for [[x y] (filter diagonal? order)]
               [(.indexOf ^clojure.lang.PersistentVector order [x y])
                (vec (keep-indexed (fn [i p] (when (#{[x 0] [0 y]} p) i)) order))]))))

(defn- not-allowed-diagonal? [at-idx adjacent-cells]
  (when-let [[a b] (get diagonal-check-indizes at-idx)]
    (and (nil? (adjacent-cells a))
         (nil? (adjacent-cells b)))))

(defn- remove-not-allowed-diagonals [adjacent-cells]
  (remove nil?
          (map-indexed
           (fn [idx cell]
             (when-not (or (nil? cell)
                           (not-allowed-diagonal? idx adjacent-cells))
               cell))
           adjacent-cells)))

(defn- filter-viable-cells [eid adjacent-cells]
  (remove-not-allowed-diagonals
   (mapv #(when-not (or (cell/pf-blocked? @%)
                        (cell/occupied-by-other? @% eid))
            %)
         adjacent-cells)))

(defn- get-min-dist-cell [distance-to cells]
  (let [cells (filter distance-to cells)]
    (when (seq cells)
      (apply min-key distance-to cells))))

(defn- viable-cell? [grid distance-to own-dist eid cell]
  (when-let [best-cell (get-min-dist-cell
                        distance-to
                        (filter-viable-cells eid (potential-field/cached-adjacent-cells grid cell)))]
    (when (< (float (distance-to best-cell)) (float own-dist))
      cell)))

(defn- find-next-cell
  "returns {:target-entity eid} or {:target-cell cell}. Cell can be nil."
  [grid eid own-cell]
  (let [faction (faction/enemy (:entity/faction @eid))
        distance-to #(potential-field/nearest-entity-distance @% faction)
        nearest-entity-at #(potential-field/nearest-entity @% faction)
        own-dist (distance-to own-cell)
        adjacent-cells (potential-field/cached-adjacent-cells grid own-cell)]
    (if (and own-dist (zero? (float own-dist)))
      {:target-entity (nearest-entity-at own-cell)}
      (if-let [adjacent-cell (first (filter #(and (distance-to %)
                                                  (zero? (float (distance-to %))))
                                            adjacent-cells))]
        {:target-entity (nearest-entity-at adjacent-cell)}
        {:target-cell (let [cells (filter-viable-cells eid adjacent-cells)
                            min-key-cell (get-min-dist-cell distance-to cells)]
                        (cond
                          (not min-key-cell) ; red
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
  (let [cells (keep #(get-in grid %) (body/touched-tiles entity))]
    (and (= 1 (count cells))
         (= cell (first cells)))))

(defn find-direction [grid eid]
  (let [position (:entity/position @eid)
        own-cell (get-in grid (mapv int position))
        {:keys [target-entity target-cell]} (find-next-cell grid eid own-cell)]
    (cond
      target-entity
      (v2/direction position (:entity/position @target-entity))

      (nil? target-cell)
      nil

      :else
      (when-not (and (= target-cell own-cell)
                     (cell/occupied-by-other? @own-cell eid))
        (when-not (inside-cell? grid @eid target-cell)
          (v2/direction position (:middle @target-cell)))))))

(defn- potential-field-step [grid faction last-marked-cells]
  (let [marked-cells (transient [])
        distance #(potential-field/nearest-entity-distance % faction)
        nearest-entity-at #(potential-field/nearest-entity % faction)
        marked? faction]
    (doseq [cell (sort-by #(distance @%) last-marked-cells)
            adjacent-cell (potential-field/cached-adjacent-cells grid cell)
            :let [cell* @cell
                  adjacent-cell* @adjacent-cell]
            :when (not (or (cell/pf-blocked? adjacent-cell*)
                           (marked? adjacent-cell*)))
            :let [distance-value (+ (float (distance cell*))
                                    (float (if (position/diagonal? (:position cell*)
                                                                   (:position adjacent-cell*))
                                             1.4 ; square root of 2 * 10
                                             1)))]]
      (swap! adjacent-cell assoc faction {:distance distance-value
                                          :eid (nearest-entity-at cell*)})
      (conj! marked-cells adjacent-cell))
    (persistent! marked-cells)))

(defn- generate-potential-field
  [grid faction tiles->entities max-iterations]
  (let [entity-cell-seq (for [[tile eid] tiles->entities]
                          [eid (get-in grid tile)])
        marked (map second entity-cell-seq)]
    (doseq [[eid cell] entity-cell-seq]
      (swap! cell assoc faction {:distance 0
                                 :eid eid}))
    (loop [marked-cells     marked
           new-marked-cells marked
           iterations 0]
      (if (= iterations max-iterations)
        marked-cells
        (let [new-marked (potential-field-step grid faction new-marked-cells)]
          (recur (concat marked-cells new-marked)
                 new-marked
                 (inc iterations)))))))

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

(defn update!
  [grid pf-cache faction entities max-iterations]
  (let [tiles->entities (let [entities (filter #(= (:entity/faction @%) faction)
                                               entities)]
                          (zipmap (map #(mapv int (:entity/position @%)) entities)
                                  entities))
        last-state   [faction :tiles->entities]
        marked-cells [faction :marked-cells]]
    (when-not (= (get-in @pf-cache last-state) tiles->entities)
      (swap! pf-cache assoc-in last-state tiles->entities)
      (doseq [cell (get-in @pf-cache marked-cells)]
        (swap! cell dissoc faction))
      (swap! pf-cache assoc-in marked-cells (generate-potential-field
                                             grid
                                             faction
                                             tiles->entities
                                             max-iterations)))))

(defn valid-position? [grid {:keys [entity/z-order] :as body} entity-id]
  (assert (:entity/collides? body))
  (let [cells* (map deref (keep #(get-in grid %) (body/touched-tiles body)))]
    (and (not-any? #(cell/blocked? % z-order) cells*)
         (->> (into #{} (mapcat :entities) cells*)
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:entity/collides? other-entity)
                                 (body/overlaps? other-entity
                                                 body)))))))))

(defn try-move [grid body entity-id movement]
  (let [{:keys [direction speed delta-time]} movement
        new-body (update body :entity/position
                         #(mapv (fn [p d] (+ p (* d speed delta-time))) % direction))]
    (when (valid-position? grid new-body entity-id)
      new-body)))

(defn try-move-solid-body [grid body entity-id {[vx vy] :direction :as movement}]
  (let [xdir (math/signum (float vx))
        ydir (math/signum (float vy))]
    (or (try-move grid body entity-id movement)
        (try-move grid body entity-id (assoc movement :direction [xdir 0]))
        (try-move grid body entity-id (assoc movement :direction [0 ydir])))))

(defn body->occupied-cells
  [grid {:keys [entity/position
                entity/width
                entity/height]
         :as body}]
  (if (or (> (float width) 1) (> (float height) 1))
    (keep #(get-in grid %) (body/touched-tiles body))
    [(get-in grid (mapv int position))]))

(defn set-occupied-cells! [grid eid]
  (let [cells (body->occupied-cells grid @eid)]
    (doseq [cell cells]
      (assert (not (get (:occupied @cell) eid)))
      (swap! cell update :occupied conj eid))
    (swap! eid assoc :entity/occupied-cells cells)))

(defn set-touched-cells! [grid eid]
  (let [cells (keep #(get-in grid %) (body/touched-tiles @eid))]
    (assert (not-any? nil? cells))
    (swap! eid assoc :entity/touched-cells cells)
    (doseq [cell cells]
      (assert (not (get (:entities @cell) eid)))
      (swap! cell update :entities conj eid))))

(defn remove-from-occupied-cells! [_ eid]
  (doseq [cell (:entity/occupied-cells @eid)]
    (assert (get (:occupied @cell) eid))
    (swap! cell update :occupied disj eid)))

(defn remove-from-touched-cells! [_ eid]
  (doseq [cell (:entity/touched-cells @eid)]
    (assert (get (:entities @cell) eid))
    (swap! cell update :entities disj eid)))

(defn point->entities [grid pos]
  (when-let [cell (get-in grid (mapv int pos))]
    (filter #(.contains ^Rectangle (body/rectangle @%) (float (first pos)) (float (second pos)))
            (:entities @cell))))
