(ns com.badlogic.gdx.graphics.pixmap
  (:require [gdl.graphics.pixmap :as pixmap])
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format
                                      Texture)))

(defn create [width height]
  (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888))

(extend-type Pixmap
  pixmap/Pixmap
  (texture [pixmap]
    (Texture. pixmap))

  (set-color! [pixmap r g b a]
    (.setColor pixmap r g b a))

  (draw-pixel! [pixmap x y]
    (.drawPixel pixmap x y))

  (dispose! [pixmap]
    (.dispose pixmap)))
