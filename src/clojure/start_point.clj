(ns clojure.start-point
  (:require [clojure.v2.add :as add]
            [clojure.v2.direction :as direction]
            [clojure.v2.scale :as scale]))

; TODO use at projectile & also adjust rotation
(defn start-point [body target-body]
  (add/f (:body/position body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  (/ (:body/width body) 2))))
