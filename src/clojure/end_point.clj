(ns clojure.end-point
  (:require [clojure.v2.add :as add]
            [clojure.v2.direction :as direction]
            [clojure.v2.scale :as scale]
            [clojure.start-point :refer [start-point]]))

(defn end-point [body target-body maxrange]
  (add/f (start-point body target-body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  maxrange)))
