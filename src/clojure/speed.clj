(ns clojure.speed
  (:require [moon.stats :as stats]))

(defn f [{:keys [entity/stats]}]
  (or (stats/get-value stats :stats/movement-speed)
      0))
