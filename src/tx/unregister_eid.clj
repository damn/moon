(ns tx.unregister-eid
  (:require [moon.content-grid :as content-grid]
            [moon.grid.remove-from-touched-cells :refer [remove-from-touched-cells!]]
            [moon.grid.remove-from-occupied-cells :refer [remove-from-occupied-cells!]]))

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
  (remove-from-touched-cells! grid eid)
  (when (:body/collides? (:entity/body @eid))
    (remove-from-occupied-cells! grid eid))
  nil)
