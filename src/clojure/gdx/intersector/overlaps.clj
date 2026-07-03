(ns clojure.gdx.intersector.overlaps
  (:import (com.badlogic.gdx.math Circle Intersector)
           (com.badlogic.gdx.math Rectangle)))

(defn f [^Circle circle ^Rectangle rectangle]
  (Intersector/overlaps circle rectangle))
