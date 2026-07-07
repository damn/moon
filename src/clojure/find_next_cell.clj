(ns clojure.find-next-cell
  (:require [clojure.nearest-entity :as nearest-entity]
            [clojure.nearest-entity-distance :as nearest-entity-distance]
            [clojure.moon-faction :as faction]
            [clojure.cached-adjacent-cells :refer [cached-adjacent-cells]]
            [clojure.filter-viable-cells :as filter-viable-cells]
            [clojure.get-min-dist-cell :refer [get-min-dist-cell]]
            [clojure.viable-cell :refer [viable-cell?]]))

(defn f
  "returns {:target-entity eid} or {:target-cell cell}. Cell can be nil."
  [grid eid own-cell]
  (let [faction (faction/enemy (:entity/faction @eid))
        distance-to    #(nearest-entity-distance/f @% faction)
        nearest-entity #(nearest-entity/f          @% faction)
        own-dist (distance-to own-cell)
        adjacent-cells (cached-adjacent-cells grid own-cell)]
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
