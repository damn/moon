(ns moon.application.create.unorganised
  (:require moon.player-item-on-cursor
            [clojure.input :as input]
            [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            clojure.gdx.scene2d.ui.data-viewer-window
            clojure.gdx.scene2d.ui.error-window
            clojure.gdx.scene2d.ui.horizontal-group
            clojure.gdx.scene2d.ui.image-button
            clojure.gdx.scene2d.ui.scroll-pane
            clojure.gdx.scene2d.ui.stack
            clojure.gdx.scene2d.ui.text-button
            clojure.gdx.scene2d.ui.widget
            clojure.gdx.scene2d.ui.window
            moon.ui.property-editor-window
            moon.ui.property-overview-window
            [moon.body :as body]
            [moon.input]
            [moon.inventory :as inventory]
            [moon.skill :as skill]
            [moon.state :as state]
            [moon.stats :as stats]
            [moon.textures :as textures]))

(defn step [ctx]
  (assoc ctx
          ; frame
          :ctx/active-entities nil
          :ctx/delta-time nil
          :ctx/ui-mouse-position nil
          :ctx/world-mouse-position nil
          ;
          :ctx/mouseover-eid nil
          ; graphics
          :ctx/unit-scale (atom 1)
          ;
          ;
          ; world
          :ctx/elapsed-time 0
          :ctx/paused? false
          :ctx/factions-iterations {:good 15 :evil 5}
          ; TODO if it doens't change put in defs?
          :ctx/max-delta 0.04
          :ctx/minimum-size 0.39
          :ctx/z-orders [:z-order/on-ground
                         :z-order/ground
                         :z-order/flying
                         :z-order/effect]
          ;
          :ctx/potential-field-cache (atom nil)
          :ctx/id-counter (atom 0)
          :ctx/entity-ids (atom {})
         ))

(defmethod state/enter :active-skill
  [[_k {:keys [skill]}] eid]
  [[:tx/sound (:skill/start-action-sound skill)]
   [:tx/set-cooldown eid skill]
   [:tx/update eid :entity/stats stats/pay-mana-cost (:skill/cost skill)]])

(defmethod state/cursor :active-skill
  [_ _eid _ctx]
  :cursors/sandclock)

(defn- creature-speed [{:keys [entity/stats]}]
  (or (stats/get-stat-value stats :stats/movement-speed)
      0))

(defmethod state/enter :npc-dead
  [_ eid]
  [[:tx/mark-destroyed eid]])

(defmethod state/cursor :stunned
  [_ _eid _ctx]
  :cursors/denied)

(defmethod state/enter :player-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defmethod state/exit :player-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

(defmethod state/cursor :player-moving
  [_ _eid _ctx]
  :cursors/walking)

(defmethod state/handle-input :player-moving
  [_ eid {:keys [ctx/input]}]
  (if-let [movement-vector (moon.input/player-movement-vector input)]
    [[:tx/assoc eid :entity/movement {:direction movement-vector
                                      :speed (creature-speed @eid)}]]
    [[:tx/event eid :no-movement-input]]))

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
        (moon.player-item-on-cursor/item-place-position world-mouse-position entity)
        (:entity/item-on-cursor entity)]])))

(defmethod state/cursor :player-item-on-cursor
  [_ _eid _ctx]
  :cursors/hand-grab)

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

(defmethod state/draw-ui-view :player-item-on-cursor
  [_ eid {:keys [ctx/input
                 ctx/stage
                 ctx/textures
                 ctx/ui-mouse-position]}]
  ; TODO see player-item-on-cursor at render layers
  ; always draw it here at right position, then render layers does not need input/stage
  ; can pass world to graphics, not handle here at application
  (when (stage/mouseover-actor stage (input/mouse-position input))
    [[:draw/texture-region
      (textures/texture-region textures (:entity/image (:entity/item-on-cursor @eid)))
      ui-mouse-position
      {:center? true}]]))

(defmethod state/handle-input :player-item-on-cursor
  [_ eid {:keys [ctx/input
                 ctx/stage]}]
  (let [mouseover-actor (stage/mouseover-actor stage (input/mouse-position input))]
    (when (and (input/button-just-pressed? input :input.buttons/left)
               (not mouseover-actor))
      [[:tx/event eid :drop-item]])))

(defn- interaction-state->txs [[k params] stage player-eid]
  (case k
    :interaction-state/mouseover-actor nil

    :interaction-state/clickable-mouseover-eid
    (let [{:keys [clicked-eid
                  in-click-range?]} params]
      (if in-click-range?
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/player
          [[:tx/toggle-inventory-visible]]

          :clickable/item
          (let [item (:entity/item @clicked-eid)]
            (cond
             (-> stage
                 (stage/find-actor "moon.ui.windows.inventory")
                 actor/visible?)
             [[:tx/sound "bfxr_takeit"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/event player-eid :pickup-item item]]

             (inventory/can-pickup-item? (:entity/inventory @player-eid) item)
             [[:tx/sound "bfxr_pickup"]
              [:tx/mark-destroyed clicked-eid]
              [:tx/pickup-item player-eid item]]

             :else
             [[:tx/sound "bfxr_denied"]
              [:tx/show-message "Your Inventory is full"]])))
        [[:tx/sound "bfxr_denied"]
         [:tx/show-message "Too far away"]]))

    :interaction-state.skill/usable
    (let [[skill effect-ctx] params]
      [[:tx/event player-eid :start-action [skill effect-ctx]]])

    :interaction-state.skill/not-usable
    (let [state params]
      [[:tx/sound "bfxr_denied"]
       [:tx/show-message (case state
                           :cooldown "Skill is still on cooldown"
                           :not-enough-mana "Not enough mana"
                           :invalid-params "Cannot use this here")]])

    :interaction-state/no-skill-selected
    [[:tx/sound "bfxr_denied"]
     [:tx/show-message "No selected skill"]]))

(defmethod state/cursor :player-idle
  [_ eid {:keys [ctx/interaction-state]}]
  (let [[k params] interaction-state]
    (case k
      :interaction-state/mouseover-actor
      (let [[actor-type params] params
            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                           (let [inventory-slot params]
                                             (get-in (:entity/inventory @eid) inventory-slot)))]
        (cond
         inventory-cell-with-item?
         :cursors/hand-before-grab

         (= actor-type :mouseover-actor/window-title-bar)
         :cursors/move-window

         (= actor-type :mouseover-actor/button)
         :cursors/over-button

         (= actor-type :mouseover-actor/unspecified)
         :cursors/default

         :else
         :cursors/default))

      :interaction-state/clickable-mouseover-eid
      (let [{:keys [clicked-eid
                    in-click-range?]} params]
        (case (:type (:entity/clickable @clicked-eid))
          :clickable/item (if in-click-range?
                            :cursors/hand-before-grab
                            :cursors/hand-before-grab-gray)
          :clickable/player :cursors/bag))

      :interaction-state.skill/usable
      :cursors/use-skill

      :interaction-state.skill/not-usable
      :cursors/skill-not-usable

      :interaction-state/no-skill-selected
      :cursors/no-skill-selected)))

(defmethod state/clicked-inventory-cell :player-idle
  [_ eid cell]
  (when-let [item (get-in (:entity/inventory @eid) cell)]
    [[:tx/sound "bfxr_takeit"]
     [:tx/event eid :pickup-item item]
     [:tx/remove-item eid cell]]))

(defmethod state/handle-input :player-idle
  [_ player-eid {:keys [ctx/input
                        ctx/interaction-state
                        ctx/stage] :as ctx}]
  (if-let [movement-vector (moon.input/player-movement-vector input)]
    [[:tx/event player-eid :movement-input movement-vector]]
    (when (input/button-just-pressed? input :input.buttons/left)
      (interaction-state->txs interaction-state
                              stage
                              player-eid))))

(defmethod state/enter :player-dead
  [_ _eid]
  [[:tx/sound "bfxr_playerdeath"]
   [:tx/show-modal {:title "YOU DIED - again!"
                    :text "Good luck next time!"
                    :button-text "OK"
                    :on-click (fn [])}]])

(defmethod state/cursor :player-dead
  [_ _eid _ctx]
  :cursors/black-x)

(defmethod state/exit :npc-sleeping
  [_ eid _ctx]
  [[:tx/spawn-alert (:body/position (:entity/body @eid)) (:entity/faction @eid) 0.2]
   [:tx/add-text-effect eid "[WHITE]!" 1]])

(defmethod state/enter :npc-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defmethod state/exit :npc-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])

; no window movable type cursor appears here like in player idle
; inventory still working, other stuff not, because custom listener to keypresses ? use actor listeners?
; => input events handling
; hmmm interesting ... can disable @ item in cursor  / moving / etc.

(comment
 (.postRunnable com.badlogic.gdx.Gdx/app
                (fn []
                  (:tx/show-modal @moon.application/state
                       {:title "TestTitle"
                        :text "TextTEXT"
                        :button-text "testbuttonTEXT"
                        :on-click (fn [])})))

 )
