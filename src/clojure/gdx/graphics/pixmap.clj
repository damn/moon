(ns clojure.gdx.graphics.pixmap
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format)))

(defn create
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height ^Pixmap$Format pixmap-format]
   (Pixmap. (int width)
            (int height)
            pixmap-format)))

(defn dispose! [^Pixmap pixmap]
  (.dispose pixmap))

(defn set-color! [^Pixmap pixmap [r g b a]]
  (.setColor pixmap r g b a))

(defn draw-pixel! [^Pixmap pixmap x y]
  (.drawPixel pixmap x y))
