(ns moon.render.remove-destroyed-entities
  (:require [moon.ctx :as ctx]
            [moon.entity :as entity]
            [moon.world.content-grid :as content-grid]
            [moon.world.grid :as grid]))

(defn- remove-destroyed-entities!
  [{:keys [world/content-grid
           world/entity-ids
           world/grid]}]
  (mapcat
   (fn [eid]
     (let [id (:entity/id @eid)]
       (assert (contains? @entity-ids id))
       (swap! entity-ids dissoc id))
     (content-grid/remove-entity! content-grid eid)
     (grid/remove-from-touched-cells! grid eid)
     (when (:body/collides? (:entity/body @eid))
       (grid/remove-from-occupied-cells! grid eid))
     (mapcat (fn [[k v]]
               (entity/destroy [k v] eid))
             @eid))
   (filter (comp :entity/destroyed? deref)
           (vals @entity-ids))))

(defn do!
  [{:keys [ctx/world]
    :as ctx}]
  (ctx/handle! ctx (remove-destroyed-entities! world))
  ctx)
