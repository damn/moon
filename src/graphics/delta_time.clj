(ns graphics.delta-time
  (:import (com.badlogic.gdx Graphics)))

(defn f [graphics]
  (.getDeltaTime ^Graphics graphics))
