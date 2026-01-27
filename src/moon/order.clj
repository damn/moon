(ns moon.order
  (:require [moon.vector :as vector]))

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
    (sort-by (fn [[k _]] (or (vector/index-of k k-order) max-count))
             components)))

(defn define-order [order-k-vector]
  (apply hash-map (interleave order-k-vector (range))))

(defn sort-by-order [coll get-item-order-k order]
  (sort-by #((get-item-order-k %) order) < coll))
