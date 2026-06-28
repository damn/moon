(ns gdx.math.rectangle.contains
  (:refer-clojure :exclude [contains?])
  (:import (com.badlogic.gdx.math Rectangle)))

(defn contains? [^Rectangle rectangle x y]
  (.contains rectangle x y))
