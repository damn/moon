(ns moon.tx.remove-item
  (:require [moon.stats :as stats]
            [moon.inventory :as inventory]))

(defn do!
  [_ctx eid cell]
  (let [entity @eid
        item (get-in (:entity/inventory entity) cell)]
    (assert item)
    (swap! eid assoc-in (cons :entity/inventory cell) nil)
    (when (inventory/applies-modifiers? cell)
      (swap! eid update :entity/stats stats/remove-mods (:stats/modifiers item)))
    nil))
