(ns render.set-camera-position
  (:require [gdx.graphics.orthographic-camera :as camera]))

(defn step
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera/set-position! (:viewport/camera world-viewport)
                        (:body/position (:entity/body @player-eid)))
  ctx)
