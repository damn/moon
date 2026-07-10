(ns gdl.pixmap
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn new [& args]
  (apply pixmap/new args))

(defn set-color! [& args]
  (apply pixmap/setColor args))

(defn draw-pixel! [& args]
  (apply pixmap/drawPixel args))

(defn get-format [& args]
  (apply pixmap/getFormat args))
