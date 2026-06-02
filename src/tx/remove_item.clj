(ns tx.remove-item
  (:require [moon.inventory :as inventory]
            [moon.stats :as stats]
            [reaction-txs.remove-item :as remove-item-reaction]))

(defn do! [ctx eid cell]
  (let [entity @eid
        item (get-in (:entity/inventory entity) cell)]
    (assert item)
    (swap! eid assoc-in (cons :entity/inventory cell) nil)
    (when (inventory/applies-modifiers? cell)
      (swap! eid update :entity/stats stats/remove-mods (:stats/modifiers item)))
    (remove-item-reaction/f ctx eid cell)
    nil))
