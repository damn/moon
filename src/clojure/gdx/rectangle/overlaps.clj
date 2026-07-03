(ns clojure.gdx.rectangle.overlaps
  (:import (com.badlogic.gdx.math Rectangle)))

(defn f [^Rectangle a ^Rectangle b]
  (.overlaps a b))
