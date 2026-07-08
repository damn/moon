(ns clojure.moon.set-camera-position
  (:require [clojure.orthographic-camera-set-position :as camera-set-position]))

(defn f
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera-set-position/set-position! (:viewport/camera world-viewport)
                                     (:body/position (:entity/body @player-eid)))
  ctx)
