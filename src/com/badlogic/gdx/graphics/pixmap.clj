(ns com.badlogic.gdx.graphics.pixmap
  (:require [gdl.graphics.pixmap :as pixmap])
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format
                                      Texture)))

(defn create [w h]
  (Pixmap. (int w) (int h) Pixmap$Format/RGBA8888))

(extend-type Pixmap
  pixmap/Pixmap
  (dispose! [pixmap]
    (.dispose pixmap))

  (set-color! [pixmap r g b a]
    (.setColor pixmap r g b a))

  (draw-pixel! [pixmap x y]
    (.drawPixel pixmap x y))

  (texture [pixmap]
    (Texture. pixmap)))
