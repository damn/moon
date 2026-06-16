(ns create.shape-drawer
  (:require [com.badlogic.gdx.graphics.texture :as texture]
            [space.earlygrey.shape-drawer :refer [shape-drawer]]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]}]
  (shape-drawer batch (texture/region shape-drawer-texture 1 0 1 1)))
