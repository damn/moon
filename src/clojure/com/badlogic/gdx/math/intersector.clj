(ns clojure.com.badlogic.gdx.math.intersector
  "Class offering various static methods for intersection testing between different geometric objects."
  (:import (com.badlogic.gdx.math Circle Intersector Rectangle)))

(defn overlaps? [^Circle circle ^Rectangle rectangle]
  (Intersector/overlaps circle rectangle))
