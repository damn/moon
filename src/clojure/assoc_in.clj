(ns clojure.assoc-in)

(defn f [_ctx eid ks value]
  (swap! eid assoc-in ks value)
  nil)
