(ns tx.set-item
  (:require [moon.inventory :as inventory]
            [moon.stats :as stats]))

(defn do! [ctx eid cell item]
  (let [entity @eid
        inventory (:entity/inventory entity)]
    (assert (and (nil? (get-in inventory cell))
                 (inventory/valid-slot? cell item)))
    [[:tx/assoc-in eid (cons :entity/inventory cell) item]
     (when (inventory/applies-modifiers? cell)
       [:tx/update eid :entity/stats stats/add (:stats/modifiers item)])
     (when (:entity/player? @eid)
       [:tx/ui-set-item eid cell item])]))
