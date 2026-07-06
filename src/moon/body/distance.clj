(ns moon.body.distance
  (:require [clojure.math.vector2.distance :as distance]))

(defn f [body other-body]
  (distance/f (:body/position body)
              (:body/position other-body)))
