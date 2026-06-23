(ns moon.body.projectile-start-point
  (:require [math.vector2.add :as add]
            [math.vector2.scale :as scale]))

(defn f [body direction size]
  (add/f (:body/position body)
         (scale/f direction
                  (+ (/ (:body/width body) 2) size 0.1))))
