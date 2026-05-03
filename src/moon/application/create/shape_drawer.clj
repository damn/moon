(ns moon.application.create.shape-drawer
  (:require [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (shape-drawer/create batch (TextureRegion. ^Texture shape-drawer-texture 1 0 1 1))))
