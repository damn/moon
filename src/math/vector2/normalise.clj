(ns math.vector2.normalise
  (:require [math.vector2.length :as length]))

(defn f [[x y :as v]]
  (let [len (length/f v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))
