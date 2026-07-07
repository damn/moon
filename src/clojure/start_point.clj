(ns clojure.start-point
  (:require [clojure.add :as add]
            [clojure.direction :as direction]
            [clojure.scale :as scale]))

; TODO use at projectile & also adjust rotation
(defn start-point [body target-body]
  (add/f (:body/position body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  (/ (:body/width body) 2))))
