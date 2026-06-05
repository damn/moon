(ns clojure.math.circle.overlaps
  (:import (com.badlogic.gdx.math Circle
                                  Intersector
                                  Rectangle)))

(defn overlaps?
  [{:keys [position radius]}
   ^Rectangle rectangle]
  (Intersector/overlaps (Circle. (position 0)
                                 (position 1)
                                 radius)
                        rectangle))
