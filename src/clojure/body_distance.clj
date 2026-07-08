(ns clojure.body-distance
  (:require [clojure.v2.distance :as distance]))

(defn f [body other-body]
  (distance/f (:body/position body)
              (:body/position other-body)))
