(ns world.unregister-eid
  (:require [moon.content-grid :as content-grid]
            [moon.grid :as grid]))

(defn unregister-eid!
  [{:keys [world/entity-ids
           world/content-grid
           world/grid]}
   eid]
  (let [id (:entity/id @eid)]
    (swap! entity-ids dissoc id)
    (content-grid/remove-entity! content-grid eid)
    (grid/remove-from-touched-cells! grid eid)
    (when (:entity/collides? @eid)
      (grid/remove-from-occupied-cells! grid eid)))
  nil)
