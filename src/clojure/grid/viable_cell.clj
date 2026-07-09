(ns clojure.grid.viable-cell
  (:require [clojure.filter-viable-cells :as filter-viable-cells]
            [clojure.get-min-dist-cell :refer [get-min-dist-cell]]
            [clojure.grid.cached-adjacent-cells :refer [cached-adjacent-cells]]))

(defn viable-cell? [grid distance-to own-dist eid cell]
  (when-let [best-cell (get-min-dist-cell
                        distance-to
                        (filter-viable-cells/f eid (cached-adjacent-cells grid cell)))]
    (when (< (float (distance-to best-cell)) (float own-dist))
      cell)))
