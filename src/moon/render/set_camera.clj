(ns moon.render.set-camera
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]))

(defn step
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera/set-position! (viewport/camera world-viewport)
                        (:body/position (:entity/body @player-eid)))
  ctx)
