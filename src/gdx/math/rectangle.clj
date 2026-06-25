(ns gdx.math.rectangle
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y width height]
  (Rectangle. x y width height))
