(ns clojure.gdx.rectangle.contains
  (:import (com.badlogic.gdx.math Rectangle)))

(defn f [^Rectangle rectangle [x y]]
  (.contains rectangle (float x) (float y)))
