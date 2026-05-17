(ns game.tx.state-exit
  (:require moon.player-item-on-cursor
            [moon.state :as state]))

(defmethod state/exit :player-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defmethod state/exit :player-item-on-cursor
  [_ eid {:keys [ctx/world-mouse-position]}]
  ; at clicked-cell when we put it into a inventory-cell
  ; we do not want to drop it on the ground too additonally,
  ; so we dissoc it there manually. Otherwise it creates another item
  ; on the ground
  (let [entity @eid]
    (when (:entity/item-on-cursor entity)
      [[:tx/sound "bfxr_itemputground"]
       [:tx/dissoc eid :entity/item-on-cursor]
       [:tx/spawn-item
        (moon.player-item-on-cursor/item-place-position world-mouse-position entity)
        (:entity/item-on-cursor entity)]])))

(defmethod state/exit :npc-sleeping
  [_ eid _ctx]
  [[:tx/spawn-alert (:body/position (:entity/body @eid)) (:entity/faction @eid) 0.2]
   [:tx/add-text-effect eid "[WHITE]!" 1]])

(defmethod state/exit :npc-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defn do! [ctx eid [state-k state-v]]
  (state/exit [state-k state-v] eid ctx))
