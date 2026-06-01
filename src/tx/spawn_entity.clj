(ns tx.spawn-entity
  (:require [game.entity :as entity]
            entity.create.animation
            entity.create.body
            entity.create.delete-after-duration
            entity.create.projectile-collision
            entity.create.stats
            [qrecord.core :as q]))

(def k->create
  {
   :entity/animation entity.create.animation/f
   :entity/body entity.create.body/f
   :entity/delete-after-duration entity.create.delete-after-duration/f
   :entity/projectile-collision entity.create.projectile-collision/f
   :entity/stats entity.create.stats/f
   }
  )

(q/defrecord Entity [entity/body])

(defn do! [ctx entity]
  (let [entity (reduce (fn [m [k v]]
                         (assoc m k (if-let [f (k->create k)]
                                      (f v ctx)
                                      v)))
                       {}
                       entity)
        entity (merge (map->Entity {}) entity)
        eid (atom entity)]
    (cons
     [:tx/register-eid eid]
     (mapcat #(entity/after-create % eid ctx) @eid))))
