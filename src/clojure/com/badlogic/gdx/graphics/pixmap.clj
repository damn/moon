(ns clojure.com.badlogic.gdx.graphics.pixmap
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap Pixmap$Format)))

(defn create
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height ^Pixmap$Format pixmap-format]
   (Pixmap. (int width) (int height) pixmap-format)))

(defn set-color! [^Pixmap pixmap r g b a]
  (.setColor pixmap (float r) (float g) (float b) (float a)))

(defn draw-pixel! [^Pixmap pixmap x y]
  (.drawPixel pixmap (int x) (int y)))
