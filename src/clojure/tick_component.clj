(ns clojure.tick-component)

(defn tick-component
  [{:keys [ctx/k->tick] :as ctx} eid [k v]]
  (if-let [f (k->tick k)]
    (f v eid ctx)
    nil))
