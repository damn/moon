(ns game.state.player-item-on-cursor
  (:require [clojure.app :as app]
            [clojure.input :as input]
            [clojure.math.vector2 :as v]
            [clojure.input.buttons :as input.buttons]
            [moon.entity :as entity]
            [moon.inventory :as inventory]
            [clojure.scene2d.stage :as stage]
            [moon.state :as state]
            [moon.textures :as textures]))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.
(defn- item-place-position
  [world-mouse-position player-entity]
  (let [player-position (:body/position (:entity/body player-entity))
        ; so you cannot put it out of your own reach
        maxrange (- (:entity/click-distance-tiles player-entity) 0.1)]
    (v/add player-position
           (v/scale (v/direction player-position world-mouse-position)
                    (min maxrange
                         (v/distance player-position world-mouse-position))))))

(defmethod state/create :player-item-on-cursor
  [[_k item] _eid _ctx]
  {:item item})

(defmethod state/enter :player-item-on-cursor
  [[_k {:keys [item]}] eid]
  [[:tx/assoc eid :entity/item-on-cursor item]])

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
        (item-place-position world-mouse-position entity)
        (:entity/item-on-cursor entity)]])))

(defmethod state/draw-ui-view :player-item-on-cursor
  [_ eid {:keys [ctx/app
                 ctx/stage
                 ctx/textures
                 ctx/ui-mouse-position]}]
  ; TODO see player-item-on-cursor at render layers
  ; always draw it here at right position, then render layers does not need input/stage
  ; can pass world to graphics, not handle here at application
  (when (stage/mouseover-actor stage (input/mouse-position (app/input app)))
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
   {:keys [ctx/app
           ctx/stage
           ctx/textures
           ctx/world-mouse-position]}]
  ; TODO do not draw here, only at UI view
  ; then graphics can draw world without stage/input
  (when-not (stage/mouseover-actor stage (input/mouse-position (app/input app)))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image item))
      (item-place-position world-mouse-position entity)
      {:center? true}]]))

(defmethod state/handle-input :player-item-on-cursor
  [_ eid {:keys [ctx/app
                 ctx/stage]}]
  (let [mouseover-actor (stage/mouseover-actor stage (input/mouse-position (app/input app)))]
    (when (and (input/button-just-pressed? (app/input app) input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))

(defmethod state/cursor :player-item-on-cursor
  [_ _eid _ctx]
  :cursors/hand-grab)
