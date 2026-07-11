(ns clojure.projectile-start-point
  (:require [moon.v2 :as v2]))

(defn f [body direction size]
  (v2/add (:body/position body)
          (v2/scale direction
                    (+ (/ (:body/width body) 2) size 0.1))))
