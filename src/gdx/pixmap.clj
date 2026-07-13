(ns gdx.pixmap
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.pixmap$format :as format]))

(defn new
  ([file-handle]
   (pixmap/new file-handle))
  ([width height pixmap-format]
   (pixmap/new width height pixmap-format)))

(defn get-format [pixmap]
  (pixmap/getFormat pixmap))

(defn set-color! [pixmap r g b a]
  (pixmap/setColor pixmap r g b a))

(defn draw-pixel! [pixmap x y]
  (pixmap/drawPixel pixmap x y))

(def rgba8888 format/RGBA8888)
