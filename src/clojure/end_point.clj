(ns clojure.end-point
  (:require [clojure.add :as add]
            [clojure.direction :as direction]
            [clojure.scale :as scale]
            [clojure.start-point :refer [start-point]]))

(defn end-point [body target-body maxrange]
  (add/f (start-point body target-body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  maxrange)))
