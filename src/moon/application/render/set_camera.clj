(ns moon.application.render.set-camera
  (:require [moon.camera :as camera]))

(defn step
  [{:keys [ctx/player-eid]
    :as ctx}]
  (camera/set-position! ctx (:body/position (:entity/body @player-eid)))
  ctx)
