(ns moon.effects.target-entity
  (:require [clojure.math.vector2 :as v]))

; TODO use at projectile & also adjust rotation
(defn start-point [body target-body]
  (v/add (:body/position body)
         (v/scale (v/direction (:body/position body)
                               (:body/position target-body))
                  (/ (:body/width body) 2))))

(defn end-point [body target-body maxrange]
  (v/add (start-point body target-body)
         (v/scale (v/direction (:body/position body)
                               (:body/position target-body))
                  maxrange)))

(defn in-range? [body target-body maxrange]
  (< (- (float (v/distance (:body/position body)
                           (:body/position target-body)))
        (float (/ (:body/width body)  2))
        (float (/ (:body/width target-body) 2)))
     (float maxrange)))
