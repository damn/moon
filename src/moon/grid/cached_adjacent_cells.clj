(ns moon.grid.cached-adjacent-cells
  (:require [clojure.grid2d.get-cells :refer [get-cells]]
            [clojure.math.position :as position]))

(defn cached-adjacent-cells [g2d cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (->> @cell
                      :position
                      position/get-8-neighbours
                      (get-cells g2d))]
      (swap! cell assoc :adjacent-cells result)
      result)))
