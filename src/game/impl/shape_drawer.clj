(ns game.impl.shape-drawer
  (:require [gdl.graphics.batch :as batch]
            [gdl.graphics.texture :as texture]))

(defn create
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (batch/shape-drawer batch (texture/region shape-drawer-texture 1 0 1 1)))
