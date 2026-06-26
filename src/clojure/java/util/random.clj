(ns clojure.java.util.random
  (:import (java.util Random)))

(defn create []
  (Random.))

(defn next-float [^Random random]
  (.nextFloat random))
