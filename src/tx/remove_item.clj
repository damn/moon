(ns tx.remove-item
  (:require [moon.inventory.is-applies-modifiers :as applies-modifiers?]
            [moon.stats.remove-mods :as remove-mods]))

(defn do! [ctx eid cell]
  (let [entity @eid
        item (get-in (:entity/inventory entity) cell)]
    (assert item)
    [[:tx/assoc-in eid (cons :entity/inventory cell) nil]
     (when (applies-modifiers?/f cell)
       [:tx/update eid :entity/stats remove-mods/f (:stats/modifiers item)])
     (when (:entity/player? @eid)
       [:tx/ui-remove-item eid cell])]))
