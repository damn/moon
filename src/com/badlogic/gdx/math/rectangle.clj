(ns com.badlogic.gdx.math.rectangle
  (:import (com.badlogic.gdx.math Rectangle)))

(defn create [x y w h]
  (Rectangle. x y w h))
