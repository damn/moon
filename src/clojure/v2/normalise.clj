(ns clojure.v2.normalise
  (:require [clojure.v2.length :as length]))

(defn f [[x y :as v]]
  (let [len (length/f v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))
