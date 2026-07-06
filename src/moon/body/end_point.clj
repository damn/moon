(ns moon.body.end-point
  (:require [clojure.math.vector2.add :as add]
            [clojure.math.vector2.direction :as direction]
            [clojure.math.vector2.scale :as scale]
            [moon.body.start-point :refer [start-point]]))

(defn end-point [body target-body maxrange]
  (add/f (start-point body target-body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  maxrange)))
