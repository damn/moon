(ns moon.tx.spawn-entity
  (:require [moon.entity :as entity]
            [qrecord.core :as q]))

(q/defrecord Entity [entity/body])

(defn do! [ctx entity]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (entity/create [k v] ctx)))
                       {}
                       entity)
        entity (merge (map->Entity {}) entity)
        eid (atom entity)]
    (cons
     [:tx/register-eid eid]
     (mapcat #(entity/after-create % eid ctx) @eid))))
