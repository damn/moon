(ns gdx.pixmap
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)))

(defn new
  ([file-handle]
   (Pixmap. ^FileHandle file-handle))
  ([width height pixmap-format]
   (Pixmap. (int width) (int height) ^Pixmap$Format pixmap-format)))

(defn get-format [pixmap]
  (Pixmap/.getFormat ^Pixmap pixmap))

(defn set-color! [pixmap r g b a]
  (.setColor ^Pixmap pixmap r g b a))

(defn draw-pixel! [pixmap x y]
  (.drawPixel ^Pixmap pixmap (int x) (int y)))

(def rgba8888 Pixmap$Format/RGBA8888)
