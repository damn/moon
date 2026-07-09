(ns clojure.java.util.random
  (:import (java.util Random)))

(defn new-random []
  (Random.))

(defn srand
  ([^Random random] (.nextFloat random))
  ([n random] (* n (srand random))))

(defn sshuffle
  "Return a random permutation of coll"
  ([coll random]
   (when coll
     (let [al (java.util.ArrayList. ^java.util.Collection coll)]
       (java.util.Collections/shuffle al random)
       (clojure.lang.RT/vector (.toArray al)))))
  ([coll]
   (sshuffle coll (new-random))))
