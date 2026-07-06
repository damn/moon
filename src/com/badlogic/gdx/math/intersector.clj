(ns com.badlogic.gdx.math.intersector
  (:import (com.badlogic.gdx.math Circle Intersector)
           (com.badlogic.gdx.math Rectangle)))

(defn overlaps? [^Circle circle ^Rectangle rectangle]
  (Intersector/overlaps circle rectangle))
