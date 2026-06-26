(ns pixmap.draw-pixel
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn f! [pixmap x y]
  (pixmap/draw-pixel! pixmap x y))
