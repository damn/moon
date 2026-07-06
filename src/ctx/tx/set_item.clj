(ns ctx.tx.set-item
  (:require [moon.inventory.is-applies-modifiers :as applies-modifiers?]
            [moon.inventory.is-valid-slot :as valid-slot?]
            [moon.stats.add-mods :as add-mods]))

(defn do! [ctx eid cell item]
  (let [entity @eid
        inventory (:entity/inventory entity)]
    (assert (and (nil? (get-in inventory cell))
                 (valid-slot?/f cell item)))
    [[:tx/assoc-in eid (cons :entity/inventory cell) item]
     (when (applies-modifiers?/f cell)
       [:tx/update eid :entity/stats add-mods/f (:stats/modifiers item)])
     (when (:entity/player? @eid)
       [:tx/ui-set-item eid cell item])]))
