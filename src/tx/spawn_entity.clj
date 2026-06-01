(ns tx.spawn-entity
  (:require entity.create.animation
            entity.create.body
            entity.create.delete-after-duration
            entity.create.projectile-collision
            entity.create.stats
            entity.after-create.fsm
            entity.after-create.inventory
            entity.after-create.skills
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

(def k->after-create
  {
   :entity/fsm entity.after-create.fsm/f
   :entity/inventory entity.after-create.inventory/f
   :entity/skills entity.after-create.skills/f
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
     (mapcat (fn [[k v]]
               (if-let [f (k->after-create k)]
                 (f v eid ctx)
                 nil))
             @eid))))
