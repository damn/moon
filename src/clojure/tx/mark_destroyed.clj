(ns clojure.tx.mark-destroyed)

(defn f [_ctx eid]
  (swap! eid assoc :entity/destroyed? true)
  nil)
