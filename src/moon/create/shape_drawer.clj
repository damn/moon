(ns moon.create.shape-drawer
  (:require [moon.shape-drawer])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn step
  [{:keys [ctx/batch
           ^Texture ctx/shape-drawer-texture]
    :as ctx}]
  (assoc ctx :ctx/shape-drawer (ShapeDrawer. batch (TextureRegion. shape-drawer-texture 1 0 1 1))))

(extend-type ShapeDrawer
  moon.shape-drawer/ShapeDrawer
  (line! [this [sx sy] [ex ey] color-float-bits]
    (.setColor this (float color-float-bits))
    (.line this (float sx) (float sy) (float ex) (float ey))))
