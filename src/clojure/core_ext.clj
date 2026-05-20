(ns clojure.core-ext
  (:import (clojure.lang PersistentVector)))

(defn index-of [k ^PersistentVector v]
  (let [idx (.indexOf v k)]
    (if (= -1 idx)
      nil
      idx)))

(comment

 ; simpler way to do 'sort-by-k-order':

 (index-of :foo [:a :b :foo :c])
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
    (sort-by (fn [[k _]] (or (index-of k k-order) max-count))
             components)))
