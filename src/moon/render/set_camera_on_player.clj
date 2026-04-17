(ns moon.render.set-camera-on-player
  (:require [clojure.viewport :as viewport]
            [clojure.camera :as camera]))

(defn do!
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera/set-position! (viewport/camera world-viewport)
                        (:body/position (:entity/body @player-eid)))
  ctx)
