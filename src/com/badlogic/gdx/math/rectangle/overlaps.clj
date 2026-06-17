(ns com.badlogic.gdx.math.rectangle.overlaps
  (:import (com.badlogic.gdx.math Rectangle)))

(defn overlaps? [a b]
  (.overlaps ^Rectangle a
             ^Rectangle b))
