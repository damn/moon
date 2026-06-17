(ns clojure.math.vector2.length
  (:require [clojure.math :as math]))

(defn f [[x y]]
  (math/sqrt (+ (* x x)
                (* y y))))
