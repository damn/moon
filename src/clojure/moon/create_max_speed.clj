(ns clojure.moon.create-max-speed
  (:require [clojure.max-delta :refer [max-delta]]
            [clojure.minimum-size :refer [minimum-size]]))

(def max-speed
  (/ minimum-size max-delta))
