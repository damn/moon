(ns moon.application.create.shape-drawer
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (shape-drawer/create batch (texture-region/create shape-drawer-texture 1 0 1 1))))
