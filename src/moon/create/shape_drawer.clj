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
  (default-line-width [this]
    (.getDefaultLineWidth this))

  (set-default-line-width! [this width]
    (.setDefaultLineWidth this width))

  (set-color! [this color-float-bits]
    (.setColor this (float color-float-bits)))

  (line! [this [sx sy] [ex ey]]
    (.line this (float sx) (float sy) (float ex) (float ey)))

  (circle! [this [x y] radius]
    (.circle this x y radius))

  (rectangle! [this x y w h]
    (.rectangle this x y w h))

  (ellipse! [this [x y] radius-x radius-y]
    (.ellipse this x y radius-x radius-y))

  (filled-circle! [this [x y] radius]
    (.filledCircle this (float x) (float y) (float radius)))

  (filled-rectangle! [this x y w h]
    (.filledRectangle this (float x) (float y) (float w) (float h)))

  (sector! [this center-x center-y radius start-radians radians]
    (.sector this center-x center-y radius start-radians radians)))
