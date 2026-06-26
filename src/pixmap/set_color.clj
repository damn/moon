(ns pixmap.set-color
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn f! [pixmap r g b a]
  (pixmap/set-color! pixmap r g b a))
