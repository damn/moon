(ns tx.unregister-eid
  (:require [moon.content-grid :as content-grid]
            [moon.grid :as grid]))

(defn do!
  [{:keys [ctx/content-grid
           ctx/entity-ids
           ctx/grid]
    :as ctx}
   eid]
  (let [id (:entity/id @eid)]
    (assert (contains? @entity-ids id))
    (swap! entity-ids dissoc id))
  (content-grid/remove-entity! content-grid eid)
  (grid/remove-from-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (grid/remove-from-occupied-cells! grid eid))
  nil)
