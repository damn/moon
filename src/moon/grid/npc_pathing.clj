(ns moon.grid.npc-pathing
  (:require [clojure.math.vector2 :as v]
            [moon.grid.npc-pathing.filter-viable-cells :as filter-viable-cells]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.faction :as faction]
            [moon.grid :as grid]
            [clojure.grid2d :as g2d]
            [clojure.math.position :as position]))

(defn- get-min-dist-cell [distance-to cells]
  (let [cells (filter distance-to cells)]
    (when (seq cells)
      (apply min-key distance-to cells))))

; rarely called -> no performance bottleneck
(defn- viable-cell? [grid distance-to own-dist eid cell]
  (when-let [best-cell (get-min-dist-cell
                        distance-to
                        (filter-viable-cells/f eid (grid/cached-adjacent-cells grid cell)))]
    (when (< (float (distance-to best-cell)) (float own-dist))
      cell)))

(defn- inside-cell? [grid entity cell]
  (let [cells (g2d/get-cells grid (body/touched-tiles (:entity/body entity)))]
    (and (= 1 (count cells))
         (= cell (first cells)))))

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
        {:target-cell (let [cells (filter-viable-cells/f eid adjacent-cells)
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

(defn find-direction [grid eid]
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
