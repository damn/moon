(ns moon.create.shape-drawer
  (:require [clj.api.space.earlygrey.shape-drawer :as sd])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn step
  [{:keys [ctx/batch
           ^Texture ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (sd/create batch (TextureRegion. shape-drawer-texture 1 0 1 1))))
