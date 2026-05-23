(ns clojure.core-ext
  (:import (clojure.lang PersistentVector)))

(defn index-of [^PersistentVector v k]
  (let [idx (.indexOf v k)]
    (if (= -1 idx)
      nil
      idx)))

(comment

 ; simpler way to do 'sort-by-k-order':

 (index-of [:a :b :foo :c] :foo)
 (contains? [:a :b :foo :c] :foo)
 (def order [:low :medium :high])
 (def items [:high :low :medium :low :high])

 ;; build order lookup map
 (def order-map (zipmap order (range)))

 (sort-by order-map items)
 ;; => (:low :low :medium :high :high)
 )

(defn sort-by-k-order [k-order components]
  (let [max-count (inc (count k-order))]
    (sort-by (fn [[k _]] (or (index-of k-order k) max-count))
             components)))
