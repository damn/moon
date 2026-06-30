(ns tx.pickup-item
  (:require [moon.item.is-stackable :as stackable?]
            [moon.item.is-valid :as valid?]
            [moon.inventory.can-pickup-item :as can-pickup-item]))

(defn do! [_ctx eid item]
  (assert (valid?/f item))
  (let [[cell cell-item] (can-pickup-item/f? (:entity/inventory @eid) item)]
    (assert cell)
    (assert (or (stackable?/f item cell-item)
                (nil? cell-item)))
    (if (stackable?/f item cell-item)
      (do
       #_(tx/stack-item ctx eid cell item))
      [[:tx/set-item eid cell item]])))
