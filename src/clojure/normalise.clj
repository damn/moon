(ns clojure.normalise
  (:require [clojure.length :as length]))

(defn f [[x y :as v]]
  (let [len (length/f v)]
    (if (zero? len)
      v
      [(/ x len)
       (/ y len)])))
