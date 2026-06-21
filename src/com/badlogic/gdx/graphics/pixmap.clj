(ns com.badlogic.gdx.graphics.pixmap
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Pixmap$Format
                                      Texture)))

(defn create [width height]
  (Pixmap. (int width) (int height) Pixmap$Format/RGBA8888))

(defn dispose! [^Pixmap pixmap]
  (.dispose pixmap))

(defn set-color! [^Pixmap pixmap r g b a]
  (.setColor pixmap r g b a))

(defn draw-pixel! [^Pixmap pixmap x y]
  (.drawPixel pixmap x y))

(defn texture [^Pixmap pixmap]
  (Texture. pixmap))
