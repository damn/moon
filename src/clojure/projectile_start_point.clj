(ns clojure.projectile-start-point
  (:require [clojure.v2.add :as add]
            [clojure.v2.scale :as scale]))

(defn f [body direction size]
  (add/f (:body/position body)
         (scale/f direction
                  (+ (/ (:body/width body) 2) size 0.1))))
