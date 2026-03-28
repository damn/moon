(ns clj.api.com.badlogic.gdx.graphics.pixmap
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Texture)))

(defn create
  ([^FileHandle file-handle]
   (Pixmap. file-handle))
  ([width height pixmap-format]
   (Pixmap. width height pixmap-format)))

(defn set-color! [^Pixmap pixmap r g b a]
  (.setColor pixmap r g b a))

(defn draw-pixel! [^Pixmap pixmap x y]
  (.drawPixel pixmap x y))

(defn texture [^Pixmap pixmap]
  (Texture. pixmap))
