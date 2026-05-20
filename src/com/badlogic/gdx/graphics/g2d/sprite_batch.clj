(ns com.badlogic.gdx.graphics.g2d.sprite-batch
  (:require com.badlogic.gdx.maps.renderer
            [gdl.graphics.batch :as batch]
            [gdl.graphics.shape-drawer :as shape-drawer])
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn create []
  (SpriteBatch.))

(extend-type SpriteBatch
  batch/Batch
  (shape-drawer [batch texture-region]
    (ShapeDrawer. batch texture-region))

  (draw-tiled-map! [batch world-unit-scale camera tiled-map color-setter]
    (com.badlogic.gdx.maps.renderer/draw! batch
                                          world-unit-scale
                                          camera
                                          tiled-map
                                          color-setter))

  (begin! [batch]
    (.begin batch))

  (end! [batch]
    (.end batch))

  (set-color! [batch r g b a]
    (.setColor batch r g b a))

  (set-projection-matrix! [batch matrix]
    (.setProjectionMatrix batch matrix))

  (draw!
    ([batch texture-region x y origin-x origin-y width height scale-x scale-y rotation]
     (.draw batch
            ^TextureRegion texture-region
            x
            y
            origin-x
            origin-y
            width
            height
            scale-x
            scale-y
            rotation))
    ([batch texture-region x y w h]
     (.draw batch ^TextureRegion texture-region (float x) (float y) (float w) (float h)))))

(extend-type ShapeDrawer
  shape-drawer/ShapeDrawer
  (set-color! [this color-float-bits]
    (.setColor this (float color-float-bits)))

  (circle! [this x y radius]
    (.circle this x y radius))

  (ellipse! [this x y radius-x radius-y]
    (.ellipse this x y radius-x radius-y))

  (filled-circle! [this x y radius]
    (.filledCircle this (float x) (float y) (float radius)))

  (filled-rectangle! [this x y w h]
    (.filledRectangle this (float x) (float y) (float w) (float h)))

  (line! [this sx sy ex ey]
    (.line this (float sx) (float sy) (float ex) (float ey)))

  (rectangle! [this x y w h]
    (.rectangle this x y w h))

  (sector! [this center-x center-y radius start-radians radians]
    (.sector this center-x center-y radius start-radians radians))

  (default-line-width [this]
    (.getDefaultLineWidth this))

  (set-default-line-width! [this width]
    (.setDefaultLineWidth this width)))
