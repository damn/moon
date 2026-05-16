(ns moon.impl.shape-drawer
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [space.earlygrey.shape-drawer :as shape-drawer]))

(defn create
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer/create batch (texture-region/create shape-drawer-texture 1 0 1 1)))
