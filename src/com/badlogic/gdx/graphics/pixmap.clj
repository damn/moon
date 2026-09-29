(ns com.badlogic.gdx.graphics.pixmap
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap Pixmap$Format)))

(defn new
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height ^Pixmap$Format format]
   (Pixmap. (int width) (int height) format)))

(defn setColor [pixmap r g b a]
  (.setColor ^Pixmap pixmap r g b a))

(defn drawPixel [pixmap x y]
  (.drawPixel ^Pixmap pixmap (int x) (int y)))

(defn getFormat [^Pixmap pixmap]
  (Pixmap/.getFormat pixmap))
