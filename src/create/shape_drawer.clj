(ns create.shape-drawer
  (:require [com.badlogic.gdx.graphics.texture :as texture]
            [space.earlygrey.shape-drawer :as shape-drawer]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1)))
