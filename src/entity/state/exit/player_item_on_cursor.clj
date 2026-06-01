(ns entity.state.exit.player-item-on-cursor
  (:require [game.ctx :as ctx]))

(defn f
  [_ eid ctx]
  ; at clicked-cell when we put it into a inventory-cell
  ; we do not want to drop it on the ground too additonally,
  ; so we dissoc it there manually. Otherwise it creates another item
  ; on the ground
  (let [entity @eid]
    (when (:entity/item-on-cursor entity)
      [[:tx/sound "bfxr_itemputground"]
       [:tx/dissoc eid :entity/item-on-cursor]
       [:tx/spawn-item
        (ctx/item-place-position ctx entity)
        (:entity/item-on-cursor entity)]])))
