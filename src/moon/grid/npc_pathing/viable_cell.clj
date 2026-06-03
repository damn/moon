(ns moon.grid.npc-pathing.viable-cell
  (:require [moon.grid.npc-pathing.filter-viable-cells :as filter-viable-cells]
            [moon.grid.npc-pathing.get-min-dist-cell :refer [get-min-dist-cell]]
            [moon.grid :as grid]))

(defn viable-cell? [grid distance-to own-dist eid cell]
  (when-let [best-cell (get-min-dist-cell
                        distance-to
                        (filter-viable-cells/f eid (grid/cached-adjacent-cells grid cell)))]
    (when (< (float (distance-to best-cell)) (float own-dist))
      cell)))
