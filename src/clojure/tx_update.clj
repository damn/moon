(ns clojure.tx-update)

(defn f [_ctx eid & params]
  (apply swap! eid update params)
  nil)
