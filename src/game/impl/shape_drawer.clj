(ns game.impl.shape-drawer
  (:require [gdl.graphics.texture :as texture]
            [space.earlygrey.shape-drawer :as shape-drawer]))

(defn create
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1)))
