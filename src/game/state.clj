(ns game.state
  (:require [clojure.input.buttons :as input.buttons]
            [game.ctx :as ctx]
            [moon.inventory :as inventory]
            [clojure.gdx.scenes.scene2d.stage :as stage]
            [moon.textures :as textures]
            [moon.stats :as stats]
            [moon.timer :as timer]))

(def reaction-time-multiplier 0.016)

(defn- apply-action-speed-modifier [{:keys [entity/stats]} skill action-time]
  (/ action-time
     (or (stats/get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))

(defmulti create
  (fn [[k _v] _eid _ctx]
    k))

(defmethod create :default
  [[_k v] _eid _ctx]
  v)

(defmethod create :active-skill
  [[_k [skill effect-ctx]] eid {:keys [ctx/elapsed-time]}]
  {:skill skill
   :effect-ctx effect-ctx
   :counter (->> skill
                 :skill/action-time
                 (apply-action-speed-modifier @eid skill)
                 (timer/create elapsed-time))})

(defmethod create :stunned
  [[_k duration] _eid {:keys [ctx/elapsed-time]}]
  {:counter (timer/create elapsed-time duration)})

(defmethod create :player-moving
  [[_k movement-vector] eid _ctx]
  {:movement-vector movement-vector})

(defmethod create :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})

(defmethod create :player-item-on-cursor
  [[_k item] _eid _ctx]
  {:item item})

(defmulti enter
  (fn [[k _v] _eid]
    k))

(defmethod enter :default
  [[_k _v] _eid]
  nil)

(defmethod enter :player-item-on-cursor
  [[_k {:keys [item]}] eid]
  [[:tx/assoc eid :entity/item-on-cursor item]])

(defmethod enter :active-skill
  [[_k {:keys [skill]}] eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])

(defmethod enter :npc-dead
  [_ eid]
  [[:tx/mark-destroyed eid]])

(defmethod enter :player-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defmethod enter :player-dead
  [_ _eid]
  [[:tx/sound "bfxr_playerdeath"]
   [:tx/show-modal {:title "YOU DIED - again!"
                    :text "Good luck next time!"
                    :button-text "OK"
                    :on-click (fn [])}]])

(defmethod enter :npc-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defmulti exit
  (fn [[k _v] _eid _ctx]
    k))

(defmethod exit :default
  [[_k _v] _eid _ctx]
  nil)

(defmethod exit :player-item-on-cursor
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

(defmethod exit :player-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defmethod exit :npc-sleeping
  [_ eid _ctx]
  [[:tx/spawn-alert (:body/position (:entity/body @eid)) (:entity/faction @eid) 0.2]
   [:tx/add-text-effect eid "[WHITE]!" 1]])

(defmethod exit :npc-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defmulti cursor
  (fn [[k _v] _eid _ctx]
    k))

(defmethod cursor :player-item-on-cursor
  [_ _eid _ctx]
  :cursors/hand-grab)

(defmulti pause-game?
  (fn [k]
    k))

(defmethod pause-game? :player-item-on-cursor
  [_]
  true)

(defmulti clicked-inventory-cell
  (fn [[k _v] _eid _cell]
    k))

(defmethod clicked-inventory-cell :default
  [_ _eid _cell]
  nil)

(defmethod clicked-inventory-cell :player-item-on-cursor
  [_ eid cell]
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
      [:tx/event eid :pickup-item item-in-cell]])))

(defmulti draw-ui-view
  (fn [[k _v] _eid _ctx]
    k))

(defmethod draw-ui-view :default
  [_ _eid _ctx]
  nil)

(defmethod draw-ui-view :player-item-on-cursor
  [_ eid {:keys [ctx/stage
                 ctx/textures
                 ctx/ui-mouse-position]
          :as ctx}]
  ; TODO see player-item-on-cursor at render layers
  ; always draw it here at right position, then render layers does not need input/stage
  ; can pass world to graphics, not handle here at application
  (when (stage/mouseover-actor stage (ctx/mouse-position ctx))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
      ui-mouse-position
      {:center? true}]]))

(defmulti handle-input
  (fn [[k _v] _eid _ctx]
    k))

(defmethod handle-input :default
  [_ _eid _ctx]
  nil)

(defmethod handle-input :player-item-on-cursor
  [_ eid {:keys [ctx/stage]
          :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (ctx/mouse-position ctx))]
    (when (and (ctx/button-just-pressed? ctx input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))
