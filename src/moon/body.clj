(ns moon.body
  (:require [clojure.math.vector2.direction :as direction]
            [clojure.math.vector2.distance :as distance]))

(defn distance [body other-body]
  (distance/f (:body/position body)
              (:body/position other-body)))

(defn direction [body other-body]
  (direction/f (:body/position body)
               (:body/position other-body)))
