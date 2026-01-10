(ns moon.world.tx.pickup-item
  (:require [moon.entity.inventory :as inventory]))

(defn do!
  [_ctx eid item]
  (inventory/assert-valid-item? item)
  (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
    (assert cell)
    (assert (or (inventory/stackable? item cell-item)
                (nil? cell-item)))
    (if (inventory/stackable? item cell-item)
      (do
       #_(tx/stack-item ctx eid cell item))
      [[:tx/set-item eid cell item]])))
