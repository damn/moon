(ns moon.entity.state.player-item-on-cursor
  (:require [clojure.math.vector2 :as v]
            [moon.entity :as entity]
            [moon.entity.state :as state]
            [moon.graphics :as graphics]
            [moon.input :as input]
            [moon.ui :as ui]))

(defmethod state/create :player-item-on-cursor
  [[_k item] _eid _world]
  {:item item})

(defn world-item? [mouseover-actor]
  (not mouseover-actor))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.
(defn- placement-point [player target maxrange]
  (v/add player
         (v/scale (v/direction player target)
                  (min maxrange
                       (v/distance player target)))))

(defn item-place-position [world-mouse-position entity]
  (placement-point (:body/position (:entity/body entity))
                   world-mouse-position
                   ; so you cannot put it out of your own reach
                   (- (:entity/click-distance-tiles entity) 0.1)))

(defmethod entity/render :player-item-on-cursor
  [[_k {:keys [item]}]
   entity
   {:keys [ctx/graphics
           ctx/input
           ctx/stage]}]
  ; TODO do not draw here, only at UI view
  ; then graphics can draw world without stage/input
  (when (world-item? (ui/mouseover-actor stage (input/mouse-position input)))
    [[:draw/texture-region
      (graphics/texture-region graphics (:entity/image item))
      (item-place-position (:graphics/world-mouse-position graphics)
                           entity)
      {:center? true}]]))

(defmethod state/enter :player-item-on-cursor
  [[_k {:keys [item]}] eid]
  [[:tx/assoc eid :entity/item-on-cursor item]])

(defmethod state/exit :player-item-on-cursor
  [_ eid {:keys [ctx/graphics]}]
  ; at clicked-cell when we put it into a inventory-cell
  ; we do not want to drop it on the ground too additonally,
  ; so we dissoc it there manually. Otherwise it creates another item
  ; on the ground
  (let [entity @eid]
    (when (:entity/item-on-cursor entity)
      [[:tx/sound "bfxr_itemputground"]
       [:tx/dissoc eid :entity/item-on-cursor]
       [:tx/spawn-item
        (item-place-position
         (:graphics/world-mouse-position graphics)
         entity)
        (:entity/item-on-cursor entity)]])))

(defmethod state/cursor :player-item-on-cursor
  [_ _eid _ctx]
  :cursors/hand-grab)
