(ns clojure.moon.k-handle-input.player-moving
  (:require [clojure.player-movement-vector :refer [player-movement-vector]]
            [clojure.speed :as speed]))

(defn f
  [eid ctx]
  (if-let [movement-vector (player-movement-vector ctx)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (speed/f @eid)}]]
    [[:tx/event eid :no-movement-input]]))
