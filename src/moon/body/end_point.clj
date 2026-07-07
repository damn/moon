(ns moon.body.end-point
  (:require [clojure.add :as add]
            [clojure.direction :as direction]
            [clojure.scale :as scale]
            [moon.body.start-point :refer [start-point]]))

(defn end-point [body target-body maxrange]
  (add/f (start-point body target-body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  maxrange)))
