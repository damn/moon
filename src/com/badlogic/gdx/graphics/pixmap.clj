(ns com.badlogic.gdx.graphics.pixmap
  (:require [gdl.graphics.pixmap :as pixmap])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)))

(defn create
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height]
   (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888)))

(extend-type Pixmap
  pixmap/Pixmap
  (set-color! [pixmap r g b a]
    (.setColor pixmap r g b a))

  (draw-pixel! [pixmap x y]
    (.drawPixel pixmap x y))

  (dispose! [pixmap]
    (.dispose pixmap)))
