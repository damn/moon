(ns create.shape-drawer
  (:require [space.earlygrey.shape-drawer :as shape-drawer]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer
         (shape-drawer/create batch (com.badlogic.gdx.graphics.g2d.TextureRegion. shape-drawer-texture 1 0 1 1))))
