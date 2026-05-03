(ns com.badlogic.gdx.graphics.pixmap
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap)))

(defn create
  ([width height ^com.badlogic.gdx.graphics.Pixmap$Format pixmap-format]
   (Pixmap. (int width) (int height) pixmap-format))
  ([^FileHandle file-handle]
   (Pixmap. file-handle)))

(def dispose! Pixmap/.dispose)

(defn set-color! [^Pixmap pixmap r g b a]
  (.setColor pixmap r g b a))

(defn draw-pixel! [^Pixmap pixmap x y]
  (.drawPixel pixmap x y))
