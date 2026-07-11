(ns clojure.start-point
  (:require [moon.v2 :as v2]))

; TODO use at projectile & also adjust rotation
(defn start-point [body target-body]
  (v2/add (:body/position body)
          (v2/scale (v2/direction (:body/position body)
                                  (:body/position target-body))
                    (/ (:body/width body) 2))))
