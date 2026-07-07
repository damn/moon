(ns moon.body.direction
  (:require [clojure.direction :as direction]))

(defn f [body other-body]
  (direction/f (:body/position body)
               (:body/position other-body)))
