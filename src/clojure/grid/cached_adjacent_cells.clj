(ns clojure.grid.cached-adjacent-cells
  (:require [moon.g2d :as g2d]
            [moon.position :as position]))

; works on cell , not g2d first param
; secondly cached-get-8-neighbours is the name
; and cell namespaced keywords!
; everywhere....
(defn cached-adjacent-cells [g2d cell]
  (if-let [result (:adjacent-cells @cell)]
    result
    (let [result (->> @cell
                      :position
                      position/get-8-neighbours
                      (g2d/get-cells g2d))]
      (swap! cell assoc :adjacent-cells result)
      result)))
