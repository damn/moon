(ns graphics.set-cursor
  (:import (com.badlogic.gdx Graphics)))

(defn f [graphics cursor]
  (.setCursor ^Graphics graphics cursor))
