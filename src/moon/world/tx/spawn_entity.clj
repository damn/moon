(ns moon.world.tx.spawn-entity
  (:require [malli.utils :as mu]
            [moon.world.content-grid :as content-grid]
            [moon.world.grid :as grid]
            [qrecord.core :as q]))

(defn- create-component [[k v] {:keys [world/create-fns] :as world}]
  (if-let [f (create-fns k)]
    (f v world)
    v))

(defn- after-create-component [[k v] eid {:keys [world/after-create-fns] :as world}]
  (when-let [f (after-create-fns k)]
    (f v eid world)))

(q/defrecord Entity [entity/body])

(defn- do!*
 [{:keys [world/content-grid
          world/entity-ids
          world/grid
          world/id-counter
          world/spawn-entity-schema]
   :as world}
  entity]
 (mu/validate-humanize spawn-entity-schema entity)
 (let [entity (reduce (fn [m [k v]]
                        (assoc m k (create-component [k v] world)))
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
   (mapcat #(after-create-component % eid world) @eid)))

(defn do! [{:keys [ctx/world]} entity]
  (do!* world entity))
