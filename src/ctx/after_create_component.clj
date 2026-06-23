(ns ctx.after-create-component)

(defn after-create-component
  [{:keys [ctx/k->after-create] :as ctx} eid [k v]]
  (if-let [f (k->after-create k)]
    (f v eid ctx)
    nil))
