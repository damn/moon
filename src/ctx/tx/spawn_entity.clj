(ns ctx.tx.spawn-entity
  (:require [moon.records.entity :as entity]
            [ctx.create-component :refer [create-component]]
            [ctx.after-create-component :refer [after-create-component]]
            [ctx.register-eid :as register-eid]))

(defn do!
  [ctx entity]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (create-component ctx k v)))
                       {}
                       entity)
        entity (merge (entity/map->R {}) entity)
        eid (atom entity)]
    (register-eid/do! ctx eid)
    (mapcat (fn [component]
              (after-create-component ctx eid component))
            @eid)))
