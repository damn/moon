(ns clojure.body.in-range
  (:require [clojure.v2.distance :as distance]))

(defn in-range? [body target-body maxrange]
  (< (- (float (distance/f (:body/position body)
                           (:body/position target-body)))
        (float (/ (:body/width body)  2))
        (float (/ (:body/width target-body) 2)))
     (float maxrange)))
