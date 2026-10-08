(ns world.register-eid
  (:require [moon.content-grid :as content-grid]
            [moon.grid :as grid]))

(defn register-eid!
  [{:keys [world/id-counter
           world/entity-ids
           world/content-grid
           world/grid]}
   eid]
  (assert (and (not (contains? @eid :entity/id))))
  (let [id (swap! id-counter inc)]
    (assert (number? id))
    (swap! eid assoc :entity/id id)
    (swap! entity-ids assoc id eid))

  (assert (:entity/position @eid))
  (content-grid/update-entity! content-grid eid)

  (assert (:entity/position @eid))
  (when (:entity/collides? @eid)
    (assert (grid/valid-position? grid @eid (:entity/id @eid))))
  (grid/set-touched-cells! grid eid)
  (when (:entity/collides? @eid)
    (grid/set-occupied-cells! grid eid))
  nil)
