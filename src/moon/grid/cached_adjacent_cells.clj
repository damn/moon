(ns moon.grid.cached-adjacent-cells
  (:require [grid2d.get-cells :refer [get-cells]]
            [clojure.position.get-8-neighbours :refer [get-8-neighbours]]))

(defn cached-adjacent-cells [g2d cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (->> @cell
                      :position
                      get-8-neighbours
                      (get-cells g2d))]
      (swap! cell assoc :adjacent-cells result)
      result)))
