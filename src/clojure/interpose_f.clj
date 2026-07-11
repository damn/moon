(ns clojure.interpose-f)

; coll

(defn interpose-f [f coll] ; TODO use interpose?
  (drop 1 (interleave (repeatedly f) coll)))
