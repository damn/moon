(ns clojure.body
  (:require [clojure.v2.direction :as direction]))

(defn direction [body other-body]
  (direction/f (:body/position body)
               (:body/position other-body)))
