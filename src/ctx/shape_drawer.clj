(ns ctx.shape-drawer
  (:require [texture.texture-region :as texture-region]
            [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer/create batch (texture-region/f shape-drawer-texture 1 0 1 1)))
