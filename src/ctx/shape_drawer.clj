(ns ctx.shape-drawer
  (:require
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]
            [com.badlogic.gdx.graphics.texture :as texture]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer/new batch (texture-region/new shape-drawer-texture 1 0 1 1)))
