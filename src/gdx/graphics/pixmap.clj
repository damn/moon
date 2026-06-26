(ns gdx.graphics.pixmap
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn f [width height]
  (pixmap/create width height))
