(ns moon.tx.spawn-entity
  (:require [malli.utils :as mu]
            [moon.entity :as entity]
            [moon.world.content-grid :as content-grid]
            [moon.world.grid :as grid]
            [qrecord.core :as q]))

(q/defrecord Entity [entity/body])

(defn- world-spawn-entity
 [{:keys [world/content-grid
          world/entity-ids
          world/grid
          world/id-counter
          world/spawn-entity-schema]
   :as world}
  entity]
 (mu/validate-humanize spawn-entity-schema entity)
 (let [entity (reduce (fn [m [k v]]
                        (assoc m k (entity/create [k v] world)))
                      {}
                      entity)
       _ (assert (and (not (contains? entity :entity/id))))
       entity (assoc entity :entity/id (swap! id-counter inc))
       entity (merge (map->Entity {}) entity)
       eid (atom entity)]
   (let [id (:entity/id @eid)]
     (assert (number? id))
     (swap! entity-ids assoc id eid))
   (content-grid/add-entity! content-grid eid)
   ; https://github.com/damn/core/issues/58
   ;(assert (valid-position? grid @eid))
   (grid/set-touched-cells! grid eid)
   (when (:body/collides? (:entity/body @eid))
     (grid/set-occupied-cells! grid eid))
   (mapcat #(entity/after-create % eid world) @eid)))

(defn do! [{:keys [ctx/world]} entity]
  (world-spawn-entity world entity))
