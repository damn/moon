(ns ^:no-doc core.entity.state
  (:require [reduce-fsm :as fsm]
            [core.utils.core :refer [readable-number safe-merge]]
            [core.ctx :refer :all]
            [core.math.vector :as v]
            [core.entity :as entity]
            [core.stage :as stage]
            [core.screens :as screens]
            [core.world.grid :as grid]
            [core.world.potential-fields :as potential-fields]
            [core.world.time :as time]
            [core.effect :refer [->npc-effect-ctx skill-usable-state effect-applicable?] :as effect]
            [core.entity.inventory :as inventory]
            [core.player.interaction-state :refer [->interaction-state]])
  (:import (com.badlogic.gdx Input$Buttons Input$Keys)))

(defsystem enter "FIXME" [_ ctx])
(defmethod enter :default [_ ctx])

(defsystem exit  "FIXME" [_ ctx])
(defmethod exit :default  [_ ctx])

;; Player-State

(defsystem player-enter "FIXME" [_])
(defmethod player-enter :default [_])

(defsystem pause-game? "FIXME" [_])
(defmethod pause-game? :default [_])

(defsystem manual-tick "FIXME" [_ ctx])
(defmethod manual-tick :default [_ ctx])

(defsystem clicked-inventory-cell "FIXME" [_ cell])
(defmethod clicked-inventory-cell :default [_ cell])

(defsystem clicked-skillmenu-skill "FIXME" [_ skill])
(defmethod clicked-skillmenu-skill :default [_ skill])

(comment
 ; graphviz required in path
 (fsm/show-fsm player-fsm)

 )

(fsm/defsm-inc ^:private player-fsm
  [[:player-idle
    :kill -> :player-dead
    :stun -> :stunned
    :start-action -> :active-skill
    :pickup-item -> :player-item-on-cursor
    :movement-input -> :player-moving]
   [:player-moving
    :kill -> :player-dead
    :stun -> :stunned
    :no-movement-input -> :player-idle]
   [:active-skill
    :kill -> :player-dead
    :stun -> :stunned
    :action-done -> :player-idle]
   [:stunned
    :kill -> :player-dead
    :effect-wears-off -> :player-idle]
   [:player-item-on-cursor
    :kill -> :player-dead
    :stun -> :stunned
    :drop-item -> :player-idle
    :dropped-item -> :player-idle]
   [:player-dead]])

(fsm/defsm-inc ^:private npc-fsm
  [[:npc-sleeping
    :kill -> :npc-dead
    :stun -> :stunned
    :alert -> :npc-idle]
   [:npc-idle
    :kill -> :npc-dead
    :stun -> :stunned
    :start-action -> :active-skill
    :movement-direction -> :npc-moving]
   [:npc-moving
    :kill -> :npc-dead
    :stun -> :stunned
    :timer-finished -> :npc-idle]
   [:active-skill
    :kill -> :npc-dead
    :stun -> :stunned
    :action-done -> :npc-idle]
   [:stunned
    :kill -> :npc-dead
    :effect-wears-off -> :npc-idle]
   [:npc-dead]])

(defcomponent :effect.entity/stun
  {:data :pos
   :let duration}
  (info-text [_ _effect-ctx]
    (str "Stuns for " (readable-number duration) " seconds"))

  (applicable? [_ {:keys [effect/target]}]
    (and target
         (:entity/state @target)))

  (do! [_ {:keys [effect/target]}]
    [[:tx/event target :stun duration]]))

(defcomponent :effect.entity/kill
  {:data :some}
  (info-text [_ _effect-ctx]
    "Kills target")

  (applicable? [_ {:keys [effect/source effect/target]}]
    (and target
         (:entity/state @target)))

  (do! [_ {:keys [effect/target]}]
    [[:tx/event target :kill]]))


; fsm throws when initial-state is not part of states, so no need to assert initial-state
; initial state is nil, so associng it. make bug report at reduce-fsm?
(defn- ->init-fsm [fsm initial-state]
  (assoc (fsm initial-state nil) :state initial-state))

(defcomponent :entity/state
  (->mk [[_ [player-or-npc initial-state]] _ctx]
    {:initial-state initial-state
     :fsm (case player-or-npc
            :state/player player-fsm
            :state/npc npc-fsm)})

  (entity/create [[k {:keys [fsm initial-state]}] eid ctx]
    [[:e/assoc eid k (->init-fsm fsm initial-state)]
     [:e/assoc eid initial-state (->mk [initial-state eid] ctx)]])

  (info-text [[_ fsm] _ctx]
    (str "[YELLOW]State: " (name (:state fsm)) "[]")))

(extend-type core.entity.Entity
  entity/State
  (state [entity*]
    (-> entity* :entity/state :state))

  (state-obj [entity*]
    (let [state-k (entity/state entity*)]
      [state-k (state-k entity*)])))

(defn- send-event! [ctx eid event params]
  (when-let [fsm (:entity/state @eid)]
    (let [old-state-k (:state fsm)
          new-fsm (fsm/fsm-event fsm event)
          new-state-k (:state new-fsm)]
      (when-not (= old-state-k new-state-k)
        (let [old-state-obj (entity/state-obj @eid)
              new-state-obj [new-state-k (->mk [new-state-k eid params] ctx)]]
          [#(exit old-state-obj %)
           #(enter new-state-obj %)
           (when (:entity/player? @eid)
             (fn [_ctx] (player-enter new-state-obj)))
           [:e/assoc eid :entity/state new-fsm]
           [:e/dissoc eid old-state-k]
           [:e/assoc eid new-state-k (new-state-obj 1)]])))))

(defcomponent :tx/event
  (do! [[_ eid event params] ctx]
    (send-event! ctx eid event params)))

(defn- draw-skill-icon [g icon entity* [x y] action-counter-ratio]
  (let [[width height] (:world-unit-dimensions icon)
        _ (assert (= width height))
        radius (/ (float width) 2)
        y (+ (float y) (float (:half-height entity*)) (float 0.15))
        center [x (+ y radius)]]
    (draw-filled-circle g center radius [1 1 1 0.125])
    (draw-sector g center radius
                 90 ; start-angle
                 (* (float action-counter-ratio) 360) ; degree
                 [1 1 1 0.5])
    (draw-image g icon [(- (float x) radius) y])))

(defn- apply-action-speed-modifier [entity* skill action-time]
  (/ action-time
     (or (entity/stat entity* (:skill/action-time-modifier-key skill))
         1)))

(defcomponent :active-skill
  {:let {:keys [eid skill effect-ctx counter]}}
  (->mk [[_ eid [skill effect-ctx]] ctx]
    {:eid eid
     :skill skill
     :effect-ctx effect-ctx
     :counter (->> skill
                   :skill/action-time
                   (apply-action-speed-modifier @eid skill)
                   (time/->counter ctx))})

  (player-enter [_]
    [[:tx/cursor :cursors/sandclock]])

  (pause-game? [_]
    false)

  (enter [_ ctx]
    [[:tx/sound (:skill/start-action-sound skill)]
     (when (:skill/cooldown skill)
       [:e/assoc-in eid [:entity/skills (:property/id skill) :skill/cooling-down?] (time/->counter ctx (:skill/cooldown skill))])
     (when-not (zero? (:skill/cost skill))
       [:tx.entity.stats/pay-mana-cost eid (:skill/cost skill)])])

  (entity/tick [_ eid context]
    (cond
     (not (effect-applicable? (safe-merge context effect-ctx) (:skill/effects skill)))
     [[:tx/event eid :action-done]
      ; TODO some sound ?
      ]

     (time/stopped? context counter)
     [[:tx/event eid :action-done]
      [:tx/effect effect-ctx (:skill/effects skill)]]))

  (entity/render-info [_ entity* g ctx]
    (let [{:keys [entity/image skill/effects]} skill]
      (draw-skill-icon g image entity* (:position entity*) (time/finished-ratio ctx counter))
      (run! #(effect/render % g (merge ctx effect-ctx)) effects))))

(defcomponent :npc-dead
  {:let {:keys [eid]}}
  (->mk [[_ eid] _ctx]
    {:eid eid})

  (enter [_ _ctx]
    [[:e/destroy eid]]))

; TODO
; split it into 3 parts
; applicable
; useful
; usable?
(defn- effect-useful? [ctx effects]
  ;(println "Check useful? for effects: " (map first effects))
  (let [applicable-effects (filter #(applicable? % ctx) effects)
        ;_ (println "applicable-effects: " (map first applicable-effects))
        useful-effect (some #(useful? % ctx) applicable-effects)]
    ;(println "Useful: " useful-effect)
    useful-effect))

(defn- npc-choose-skill [ctx entity*]
  (->> entity*
       :entity/skills
       vals
       (sort-by #(or (:skill/cost %) 0))
       reverse
       (filter #(and (= :usable (skill-usable-state ctx entity* %))
                     (effect-useful? ctx (:skill/effects %))))
       first))

(comment
 (let [uid 76
       ctx @app/state
       entity* @(get-entity ctx uid)
       effect-ctx (->npc-effect-ctx ctx entity*)]
   (npc-choose-skill (safe-merge ctx effect-ctx) entity*))
 )

(defcomponent :npc-idle
  {:let {:keys [eid]}}
  (->mk [[_ eid] _ctx]
    {:eid eid})

  (entity/tick [_ eid ctx]
    (let [entity* @eid
          effect-ctx (->npc-effect-ctx ctx entity*)]
      (if-let [skill (npc-choose-skill (safe-merge ctx effect-ctx) entity*)]
        [[:tx/event eid :start-action [skill effect-ctx]]]
        [[:tx/event eid :movement-direction (potential-fields/follow-to-enemy ctx eid)]]))))

; npc moving is basically a performance optimization so npcs do not have to check
; pathfindinusable skills every frame
; also prevents fast twitching around changing directions every frame
(defcomponent :npc-moving
  {:let {:keys [eid movement-vector counter]}}
  (->mk [[_ eid movement-vector] ctx]
    {:eid eid
     :movement-vector movement-vector
     :counter (time/->counter ctx (* (entity/stat @eid :stats/reaction-time) 0.016))})

  (enter [_ _ctx]
    [[:tx/set-movement eid {:direction movement-vector
                            :speed (or (entity/stat @eid :stats/movement-speed) 0)}]])

  (exit [_ _ctx]
    [[:tx/set-movement eid nil]])

  (entity/tick [_ eid ctx]
    (when (time/stopped? ctx counter)
      [[:tx/event eid :timer-finished]])))

(defcomponent :npc-sleeping
  {:let {:keys [eid]}}
  (->mk [[_ eid] _ctx]
    {:eid eid})

  (exit [_ ctx]
    [[:tx/add-text-effect eid "[WHITE]!"]
     [:tx/shout (:position @eid) (:entity/faction @eid) 0.2]])

  (entity/tick [_ eid context]
    (let [entity* @eid
          cell ((:context/grid context) (entity/tile entity*))]
      (when-let [distance (grid/nearest-entity-distance @cell (entity/enemy-faction entity*))]
        (when (<= distance (entity/stat entity* :stats/aggro-range))
          [[:tx/event eid :alert]]))))

  (entity/render-above [_ entity* g _ctx]
    (let [[x y] (:position entity*)]
      (draw-text g
                 {:text "zzz"
                  :x x
                  :y (+ y (:half-height entity*))
                  :up? true}))))

(defcomponent :player-dead
  (player-enter [_]
    [[:tx/cursor :cursors/black-x]])

  (pause-game? [_]
    true)

  (enter [_ _ctx]
    [[:tx/sound "sounds/bfxr_playerdeath.wav"]
     [:tx/player-modal {:title "YOU DIED"
                        :text "\nGood luck next time"
                        :button-text ":("
                        :on-click #(screens/change-screen % :screens/main-menu)}]]))

(defn- add-vs [vs]
  (v/normalise (reduce v/add [0 0] vs)))

(defn- WASD-movement-vector []
  (let [r (if (.isKeyPressed gdx-input Input$Keys/D) [1  0])
        l (if (.isKeyPressed gdx-input Input$Keys/A) [-1 0])
        u (if (.isKeyPressed gdx-input Input$Keys/W) [0  1])
        d (if (.isKeyPressed gdx-input Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (add-vs (remove nil? [r l u d]))]
        (when (pos? (v/length v))
          v)))))

(defcomponent :player-idle
  {:let {:keys [eid]}}
  (->mk [[_ eid] _ctx]
    {:eid eid})

  (pause-game? [_]
    true)

  (manual-tick [_ ctx]
    (if-let [movement-vector (WASD-movement-vector)]
      [[:tx/event eid :movement-input movement-vector]]
      (let [[cursor on-click] (->interaction-state ctx @eid)]
        (cons [:tx/cursor cursor]
              (when (.isButtonJustPressed gdx-input Input$Buttons/LEFT)
                (on-click))))))

  (clicked-inventory-cell [_ cell]
    ; TODO no else case
    (when-let [item (get-in (:entity/inventory @eid) cell)]
      [[:tx/sound "sounds/bfxr_takeit.wav"]
       [:tx/event eid :pickup-item item]
       [:tx/remove-item eid cell]]))

  (clicked-skillmenu-skill [_ skill]
    (let [free-skill-points (:entity/free-skill-points @eid)]
      ; TODO no else case, no visible free-skill-points
      (when (and (pos? free-skill-points)
                 (not (entity/has-skill? @eid skill)))
        [[:e/assoc eid :entity/free-skill-points (dec free-skill-points)]
         [:tx/add-skill eid skill]]))))

(defn- clicked-cell [{:keys [entity/id] :as entity*} cell]
  (let [inventory (:entity/inventory entity*)
        item (get-in inventory cell)
        item-on-cursor (:entity/item-on-cursor entity*)]
    (cond
     ; PUT ITEM IN EMPTY CELL
     (and (not item)
          (inventory/valid-slot? cell item-on-cursor))
     [[:tx/sound "sounds/bfxr_itemput.wav"]
      [:tx/set-item id cell item-on-cursor]
      [:e/dissoc id :entity/item-on-cursor]
      [:tx/event id :dropped-item]]

     ; STACK ITEMS
     (and item (inventory/stackable? item item-on-cursor))
     [[:tx/sound "sounds/bfxr_itemput.wav"]
      [:tx/stack-item id cell item-on-cursor]
      [:e/dissoc id :entity/item-on-cursor]
      [:tx/event id :dropped-item]]

     ; SWAP ITEMS
     (and item
          (inventory/valid-slot? cell item-on-cursor))
     [[:tx/sound "sounds/bfxr_itemput.wav"]
      [:tx/remove-item id cell]
      [:tx/set-item id cell item-on-cursor]
      ; need to dissoc and drop otherwise state enter does not trigger picking it up again
      ; TODO? coud handle pickup-item from item-on-cursor state also
      [:e/dissoc id :entity/item-on-cursor]
      [:tx/event id :dropped-item]
      [:tx/event id :pickup-item item]])))

; It is possible to put items out of sight, losing them.
; Because line of sight checks center of entity only, not corners
; this is okay, you have thrown the item over a hill, thats possible.

(defn- placement-point [player target maxrange]
  (v/add player
         (v/scale (v/direction player target)
                  (min maxrange
                       (v/distance player target)))))

(defn- item-place-position [ctx entity*]
  (placement-point (:position entity*)
                   (world-mouse-position ctx)
                   ; so you cannot put it out of your own reach
                   (- (:entity/click-distance-tiles entity*) 0.1)))

(defn- world-item? [ctx]
  (not (stage/mouse-on-actor? ctx)))

(defcomponent :player-item-on-cursor
  {:let {:keys [eid item]}}
  (->mk [[_ eid item] _ctx]
    {:eid eid
     :item item})

  (pause-game? [_]
    true)

  (manual-tick [_ ctx]
    (when (and (.isButtonJustPressed gdx-input Input$Buttons/LEFT)
               (world-item? ctx))
      [[:tx/event eid :drop-item]]))

  (clicked-inventory-cell [_ cell]
    (clicked-cell @eid cell))

  (enter [_ _ctx]
    [[:tx/cursor :cursors/hand-grab]
     [:e/assoc eid :entity/item-on-cursor item]])

  (exit [_ ctx]
    ; at context.ui.inventory-window/clicked-cell when we put it into a inventory-cell
    ; we do not want to drop it on the ground too additonally,
    ; so we dissoc it there manually. Otherwise it creates another item
    ; on the ground
    (let [entity* @eid]
      (when (:entity/item-on-cursor entity*)
        [[:tx/sound "sounds/bfxr_itemputground.wav"]
         [:tx/item (item-place-position ctx entity*) (:entity/item-on-cursor entity*)]
         [:e/dissoc eid :entity/item-on-cursor]])))

  (entity/render-below [_ entity* g ctx]
    (when (world-item? ctx)
      (draw-centered-image g (:entity/image item) (item-place-position ctx entity*)))))

(defn draw-item-on-cursor [g ctx]
  (let [player-entity* (player-entity* ctx)]
    (when (and (= :player-item-on-cursor (entity/state player-entity*))
               (not (world-item? ctx)))
      (draw-centered-image g
                           (:entity/image (:entity/item-on-cursor player-entity*))
                           (gui-mouse-position ctx)))))

(defcomponent :player-moving
  {:let {:keys [eid movement-vector]}}
  (->mk [[_ eid movement-vector] _ctx]
    {:eid eid
     :movement-vector movement-vector})

  (player-enter [_]
    [[:tx/cursor :cursors/walking]])

  (pause-game? [_]
    false)

  (enter [_ _ctx]
    [[:tx/set-movement eid {:direction movement-vector
                            :speed (entity/stat @eid :stats/movement-speed)}]])

  (exit [_ _ctx]
    [[:tx/set-movement eid nil]])

  (entity/tick [_ eid context]
    (if-let [movement-vector (WASD-movement-vector)]
      [[:tx/set-movement eid {:direction movement-vector
                              :speed (entity/stat @eid :stats/movement-speed)}]]
      [[:tx/event eid :no-movement-input]])))

(defcomponent :stunned
  {:let {:keys [eid counter]}}
  (->mk [[_ eid duration] ctx]
    {:eid eid
     :counter (time/->counter ctx duration)})

  (player-enter [_]
    [[:tx/cursor :cursors/denied]])

  (pause-game? [_]
    false)

  (entity/tick [_ eid ctx]
    (when (time/stopped? ctx counter)
      [[:tx/event eid :effect-wears-off]]))

  (entity/render-below [_ entity* g _ctx]
    (draw-circle g (:position entity*) 0.5 [1 1 1 0.6])))
