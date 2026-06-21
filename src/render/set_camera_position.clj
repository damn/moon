(ns render.set-camera-position
  (:require [clojure.orthographic-camera.set-position :refer [set-position!]]))

(defn step
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (set-position! (:viewport/camera world-viewport)
                 (:body/position (:entity/body @player-eid)))
  ctx)
