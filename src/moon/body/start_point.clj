(ns moon.body.start-point
  (:require [math.vector2.add :as add]
            [math.vector2.direction :as direction]
            [math.vector2.scale :as scale]))

; TODO use at projectile & also adjust rotation
(defn start-point [body target-body]
  (add/f (:body/position body)
         (scale/f (direction/f (:body/position body)
                               (:body/position target-body))
                  (/ (:body/width body) 2))))
