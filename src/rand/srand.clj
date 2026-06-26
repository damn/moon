(ns rand.srand
  (:require [clojure.java.util.random :as random]))

(defn srand
  ([random] (random/next-float random))
  ([n random] (* n (srand random))))
