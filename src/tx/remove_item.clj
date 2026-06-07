(ns tx.remove-item
  (:require [moon.inventory :as inventory]
            [moon.stats :as stats]))

(defn do! [ctx eid cell]
  (let [entity @eid
        item (get-in (:entity/inventory entity) cell)]
    (assert item)
    [[:tx/assoc-in eid (cons :entity/inventory cell) nil]
     (when (inventory/applies-modifiers? cell)
       [:tx/update eid :entity/stats stats/remove-mods (:stats/modifiers item)])
     (when (:entity/player? @eid)
       [:tx/ui-remove-item eid cell])]))
