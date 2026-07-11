(ns clojure.moon.state-enter.player-moving
  (:require [moon.stats :as stats]))

(defn f
  [{:keys [movement-vector]} eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])
