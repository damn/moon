(ns moon.body
  (:require [clojure.math.vector2 :as v]))

(defn distance [body other-body]
  (v/distance (:body/position body)
              (:body/position other-body)))

(defn direction [body other-body]
  (v/direction (:body/position body)
               (:body/position other-body)))
