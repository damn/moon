(ns moon.body.distance
  (:require [clojure.distance :as distance]))

(defn f [body other-body]
  (distance/f (:body/position body)
              (:body/position other-body)))
