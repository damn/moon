(ns pixmap.dispose
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn f! [pixmap]
  (pixmap/dispose pixmap))
