(ns moon.create.shape-drawer
  (:require [clj.api.com.badlogic.gdx.graphics.texture :as texture]
            [clj.api.space.earlygrey.shape-drawer :as shape-drawer]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1))))
