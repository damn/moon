(ns tx.set-item
  (:require [moon.inventory :as inventory]
            [moon.stats :as stats]
            [reaction-txs.set-item :as set-item-reaction]))

(defn do! [ctx eid cell item]
  (let [entity @eid
        inventory (:entity/inventory entity)]
    (assert (and (nil? (get-in inventory cell))
                 (inventory/valid-slot? cell item)))
    (swap! eid assoc-in (cons :entity/inventory cell) item)
    (when (inventory/applies-modifiers? cell)
      (swap! eid update :entity/stats stats/add (:stats/modifiers item)))
    (set-item-reaction/f ctx eid cell item)
    nil))
