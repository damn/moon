(ns moon.body
  (:require [clojure.math.vector2 :as v]
            [clojure.math.vector2.distance :as distance]))

(defn distance [body other-body]
  (distance/f (:body/position body)
              (:body/position other-body)))

(defn direction [body other-body]
  (v/direction (:body/position body)
               (:body/position other-body)))
