(ns clojure.math.circle.overlaps
  (:require [clojure.gdx.math.circle :as circle])
  (:import (com.badlogic.gdx.math Intersector
                                  Rectangle)))

(defn overlaps? [circle ^Rectangle rectangle]
  (Intersector/overlaps (circle/create circle)
                        rectangle))
