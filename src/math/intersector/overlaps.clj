(ns math.intersector.overlaps
  (:import (com.badlogic.gdx.math Circle
                                  Intersector
                                  Rectangle)))

(defn overlaps?
  [^Circle circle
   ^Rectangle rectangle]
  (Intersector/overlaps circle
                        rectangle))
