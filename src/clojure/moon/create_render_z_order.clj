(ns clojure.moon.create-render-z-order
  (:require [clojure.moon.z-orders :refer [z-orders]]))

(def render-z-order
  (apply hash-map (interleave z-orders (range))))
