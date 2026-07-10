(ns clojure.body
  (:require [clojure.v2.add :as add]
            [clojure.v2.direction :as direction]
            [clojure.v2.scale :as scale]
            [clojure.v2.distance :as distance]
            [clojure.start-point :refer [start-point]]))

(defn direction [body other-body]
  (direction/f (:body/position body)
               (:body/position other-body)))

(defn end-point [body target-body maxrange]
  (add/f (start-point body target-body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  maxrange)))

(defn in-range? [body target-body maxrange]
  (< (- (float (distance/f (:body/position body)
                           (:body/position target-body)))
        (float (/ (:body/width body)  2))
        (float (/ (:body/width target-body) 2)))
     (float maxrange)))
