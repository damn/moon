(ns tx.spawn-entity
  (:require [qrecord.core :as q]
            [game.ctx.register-eid :as register-eid]))

(q/defrecord Entity [entity/body])

(defn do!
  [{:keys [ctx/k->create
           ctx/k->after-create]
    :as ctx}
   entity]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (if-let [f (k->create k)]
                                      (f v ctx)
                                      v)))
                       {}
                       entity)
        entity (merge (map->Entity {}) entity)
        eid (atom entity)]
    (register-eid/do! ctx eid)
    (mapcat (fn [[k v]]
              (if-let [f (k->after-create k)]
                (f v eid ctx)
                nil))
            @eid)))
