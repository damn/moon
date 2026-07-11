(ns moon.rand
  (:import (java.util Random)))

(defn new-random []
  (Random.))

(defn srand
  ([^Random random] (.nextFloat random))
  ([n random] (* n (srand random))))

(defn srand-int [n random]
  (int (srand n random)))

(defn sshuffle
  "Return a random permutation of coll"
  ([coll random]
   (when coll
     (let [al (java.util.ArrayList. ^java.util.Collection coll)]
       (java.util.Collections/shuffle al random)
       (clojure.lang.RT/vector (.toArray al)))))
  ([coll]
   (sshuffle coll (new-random))))

(defn int-between
  "returns a random integer between lower and upper bounds inclusive."
  ([[lower upper]]
   (int-between lower upper))
  ([lower upper]
   (+ lower (rand-int (inc (- upper lower))))))

(defn get-rand-weighted-item
  "given a sequence of items and their weight, returns a weighted random item.
  for example {:a 5 :b 1} returns b only in about 1 of 6 cases"
  [weights]
  (let [result (rand-int (reduce + (map #(% 1) weights)))]
    (loop [r 0
           items weights]
      (let [[item weight] (first items)
            r (+ r weight)]
        (if (> r result)
          item
          (recur (int r) (rest items)))))))
