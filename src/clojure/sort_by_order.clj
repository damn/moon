(ns clojure.sort-by-order)

(defn f [coll get-item-order-k order]
  (sort-by #((get-item-order-k %) order) < coll))
