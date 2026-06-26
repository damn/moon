(ns clojure.define-order)

(defn f [order-k-vector]
  (apply hash-map (interleave order-k-vector (range))))
