(ns game.state.player-item-on-cursor
  (:require [clojure.input.buttons :as input.buttons]
            [moon.ctx :as ctx]
            [moon.entity :as entity]
            [moon.inventory :as inventory]
            [clojure.scene2d.stage :as stage]
            [moon.state :as state]
            [moon.textures :as textures]))

(defmethod state/create :player-item-on-cursor
  [[_k item] _eid _ctx]
  {:item item})

(defmethod state/enter :player-item-on-cursor
  [[_k {:keys [item]}] eid]
  [[:tx/assoc eid :entity/item-on-cursor item]])

(defmethod state/exit :player-item-on-cursor
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

(defmethod state/draw-ui-view :player-item-on-cursor
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

(defmethod state/clicked-inventory-cell :player-item-on-cursor
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

(defmethod state/pause-game? :player-item-on-cursor
  [_]
  true)

(defmethod entity/render :player-item-on-cursor
  [[_k {:keys [item]}]
   entity
   {:keys [ctx/stage
           ctx/textures]
    :as ctx}]
  ; TODO do not draw here, only at UI view
  ; then graphics can draw world without stage/input
  (when-not (stage/mouseover-actor stage (ctx/mouse-position ctx))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image item))
      (ctx/item-place-position ctx entity)
      {:center? true}]]))

(defmethod state/handle-input :player-item-on-cursor
  [_ eid {:keys [ctx/stage]
          :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (ctx/mouse-position ctx))]
    (when (and (ctx/button-just-pressed? ctx input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))

(defmethod state/cursor :player-item-on-cursor
  [_ _eid _ctx]
  :cursors/hand-grab)
