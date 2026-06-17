(ns com.badlogic.gdx.graphics.get-gl20
  (:import (com.badlogic.gdx Graphics)))

(defn f [^Graphics graphics]
  (.getGL20 graphics))
