(ns moon.tx.update)

(defn do! [_ctx eid & params]
  (apply swap! eid update params)
  nil)
