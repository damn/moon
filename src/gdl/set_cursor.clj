(ns gdl.set-cursor
  (:import (com.badlogic.gdx Graphics)))

(defn f [^Graphics graphics cursor]
  (.setCursor graphics cursor))
