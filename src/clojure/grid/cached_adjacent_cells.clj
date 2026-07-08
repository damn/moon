(ns clojure.grid.cached-adjacent-cells
  (:require [clojure.get-cells :refer [get-cells]]
            [clojure.get-8-neighbours :refer [get-8-neighbours]]))

; works on cell , not g2d first param
; secondly cached-get-8-neighbours is the name
; and cell namespaced keywords!
; everywhere....
(defn cached-adjacent-cells [g2d cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (->> @cell
                      :position
                      get-8-neighbours
                      (get-cells g2d))]
      (swap! cell assoc :adjacent-cells result)
      result)))
