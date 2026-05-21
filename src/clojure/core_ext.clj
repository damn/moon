(ns clojure.core-ext
  (:require [clojure.lang.persistent-vector :as v]))

(comment

 ; simpler way to do 'sort-by-k-order':

 (v/index-of [:a :b :foo :c] :foo)
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
    (sort-by (fn [[k _]] (or (v/index-of k-order k) max-count))
             components)))
