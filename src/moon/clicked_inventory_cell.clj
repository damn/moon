(ns moon.clicked-inventory-cell
  (:require [moon.ctx :as ctx]
            [moon.inventory :as inventory]))

(let [fn-map {:player-idle           (fn [eid cell]
                                       (when-let [item (get-in (:entity/inventory @eid) cell)]
                                         [[:tx/sound "bfxr_takeit"]
                                          [:tx/event eid :pickup-item item]
                                          [:tx/remove-item eid cell]]))

              :player-item-on-cursor (fn [eid cell]
                                       (let [entity @eid
                                             inventory (:entity/inventory entity)
                                             item-in-cell (get-in inventory cell)
                                             item-on-cursor (:entity/item-on-cursor entity)]
                                         (cond
                                          ; PUT ITEM IN EMPTY CELL
                                          (and (not item-in-cell)
                                               (inventory/valid-slot? cell item-on-cursor))
                                          [[:tx/sound "bfxr_itemput"]
                                           [:tx/dissoc eid :entity/item-on-cursor]
                                           [:tx/set-item eid cell item-on-cursor]
                                           [:tx/event eid :dropped-item]]

                                          ; STACK ITEMS
                                          (and item-in-cell
                                               (inventory/stackable? item-in-cell item-on-cursor))
                                          [[:tx/sound "bfxr_itemput"]
                                           [:tx/dissoc eid :entity/item-on-cursor]
                                           [:tx/stack-item eid cell item-on-cursor]
                                           [:tx/event eid :dropped-item]]

                                          ; SWAP ITEMS
                                          (and item-in-cell
                                               (inventory/valid-slot? cell item-on-cursor))
                                          [[:tx/sound "bfxr_itemput"]
                                           ; need to dissoc and drop otherwise state enter does not trigger picking it up again
                                           ; TODO? coud handle pickup-item from item-on-cursor state also
                                           [:tx/dissoc eid :entity/item-on-cursor]
                                           [:tx/remove-item eid cell]
                                           [:tx/set-item eid cell item-on-cursor]
                                           [:tx/event eid :dropped-item]
                                           [:tx/event eid :pickup-item item-in-cell]])))}]
  (defn state->clicked-inventory-cell [[k v] eid cell]
    (when-let [f (k fn-map)]
      (f eid cell))))

(defn do! [cell {:keys [ctx/world] :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))]
    (ctx/handle! ctx
                 (state->clicked-inventory-cell [state-k (state-k entity)]
                                                eid
                                                cell))))
