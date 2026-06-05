(ns clojure.gdx.math.rectangle
  (:import (com.badlogic.gdx.math Rectangle)))

(defn rectangle [[x y width height]]
  (Rectangle. x y width height))
