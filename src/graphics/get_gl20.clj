(ns graphics.get-gl20
  (:import (com.badlogic.gdx Graphics)))

(defn f [graphics]
  (.getGL20 ^Graphics graphics))
