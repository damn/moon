(ns clojure.moon.set-camera-position
  (:require [clojure.orthographic-camera-set-position :as camera-set-position]
            [clojure.viewport :as viewport]))

(defn f
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera-set-position/set-position! (viewport/get-camera world-viewport)
                                     (:body/position (:entity/body @player-eid)))
  ctx)
