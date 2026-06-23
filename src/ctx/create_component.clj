(ns ctx.create-component)

(defn create-component
  [{:keys [ctx/k->create] :as ctx} k v]
  (if-let [f (k->create k)]
    (f v ctx)
    v))
