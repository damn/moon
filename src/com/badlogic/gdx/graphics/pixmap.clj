(ns com.badlogic.gdx.graphics.pixmap
  (:require [com.badlogic.gdx.graphics.pixmap$format :as format]
            [com.badlogic.gdx.files.file-handle :as file-handle])
  (:import (com.badlogic.gdx.graphics Pixmap)))

(defn create [width height]
  (Pixmap. (int width) (int height) format/rgba8888))

(defn create-from-file [file-handle]
  (Pixmap. (file-handle/type-hint file-handle)))

(defn set-color! [^Pixmap pixmap r g b a]
  (.setColor pixmap r g b a))

(defn draw-pixel! [^Pixmap pixmap x y]
  (.drawPixel pixmap x y))

(defn dispose [^Pixmap pixmap]
  (.dispose pixmap))

(defn type-hint
  ^Pixmap
  [obj]
  obj)
