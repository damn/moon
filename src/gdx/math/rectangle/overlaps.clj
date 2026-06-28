(ns gdx.math.rectangle.overlaps
  (:import (com.badlogic.gdx.math Rectangle)))

(defn overlaps? [^Rectangle a ^Rectangle b]
  (.overlaps a b))
