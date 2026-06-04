(ns tx.spawn-entity
  (:require [qrecord.core :as q]
            [game.ctx.create-component :refer [create-component]]
            [game.ctx.after-create-component :refer [after-create-component]]
            [game.ctx.register-eid :as register-eid]))

(q/defrecord Entity [entity/body])

(defn do!
  [ctx entity]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (create-component ctx k v)))
                       {}
                       entity)
        entity (merge (map->Entity {}) entity)
        eid (atom entity)]
    (register-eid/do! ctx eid)
    (mapcat (fn [component]
              (after-create-component ctx eid component))
            @eid)))
