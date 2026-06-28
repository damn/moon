(ns rand.srand
  (:import (java.util Random)))

(defn srand
  ([^Random random] (.nextFloat random))
  ([n random] (* n (srand random))))
