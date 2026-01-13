(ns moon.listener.render
  (:require [clojure.math.vector2 :as v]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.animation :as animation]
            [moon.body :as body]
            [moon.color :as color]
            [moon.ctx :as ctx]
            [moon.effects.target-all :as target-all]
            [moon.effects.target-entity :as target-entity]
            [moon.entity.state.player-item-on-cursor :as player-item-on-cursor]
            [moon.entity.stats :as stats]
            [moon.faction :as faction]
            [moon.graphics :as graphics]
            [moon.input :as input]
            [moon.inventory :as inventory]
            [moon.skill :as skill]
            [moon.throwable :as throwable]
            [moon.timer :as timer]
            [moon.ui :as ui]
            [moon.utils :as utils]
            [moon.val-max :as val-max]
            [moon.world :as world]
            [moon.world.raycaster :as raycaster])
  (:import (com.badlogic.gdx Input$Buttons)
           (moon Stage)))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/db :some]
    [:ctx/world :some]]))

(defn- validate [ctx]
  (mu/validate-humanize schema ctx)
  ctx)

(defn- get-stage-ctx
  [{:keys [ctx/stage]
    :as ctx}]
  (or (ui/get-ctx stage)
      ctx)) ; first render stage does not have ctx set.

(defn- update-mouse
  [{:keys [ctx/graphics
           ctx/input]
    :as ctx}]
  (let [mp (input/mouse-position input)]
    (-> ctx
        (assoc-in [:ctx/graphics :graphics/world-mouse-position] (graphics/unproject-world graphics mp))
        (assoc-in [:ctx/graphics :graphics/ui-mouse-position] (graphics/unproject-ui graphics mp)))))

(defn- update-mouseover-eid
  [{:keys [ctx/graphics
           ctx/input
           ctx/stage
           ctx/world]
    :as ctx}]
  (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))
        new-eid (if mouseover-actor
                  nil
                  (world/mouseover-entity world (:graphics/world-mouse-position graphics)))]
    (when-let [mouseover-eid (:world/mouseover-eid world)]
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc-in ctx [:ctx/world :world/mouseover-eid] new-eid)))

(defn- check-open-debug
  [{:keys [ctx/graphics
           ctx/input
           ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (when (input/button-just-pressed? input (:open-debug-button input/controls))
    (let [data (or (and (:world/mouseover-eid world) @(:world/mouseover-eid world))
                   @((:world/grid world) (mapv int (:graphics/world-mouse-position graphics))))]
      (ui/show-data-viewer! stage data skin)))
  ctx)

(defn- assoc-active-entities
  [{:keys [ctx/world]
    :as ctx}]
  (update ctx :ctx/world world/cache-active-entities))

(defn- set-camera-on-player
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (graphics/set-position! graphics (world/player-position world))
  ctx)

(defn- clear-screen!
  [{:keys [ctx/graphics] :as ctx}]
  (graphics/clear-screen! graphics color/black)
  ctx)

(defn- tile-color-setter
  [{:keys [ray-blocked?
           explored-tile-corners
           light-position
           see-all-tiles?
           explored-tile-color
           visible-tile-color
           invisible-tile-color]}]
  #_(reset! do-once false)
  (let [light-cache (atom {})]
    (fn tile-color-setter [_color x y]
      (let [position [(int x) (int y)]
            explored? (get @explored-tile-corners position) ; TODO needs int call ?
            base-color (if explored?
                         explored-tile-color
                         invisible-tile-color)
            cache-entry (get @light-cache position :not-found)
            blocked? (if (= cache-entry :not-found)
                       (let [blocked? (ray-blocked? light-position position)]
                         (swap! light-cache assoc position blocked?)
                         blocked?)
                       cache-entry)]
        #_(when @do-once
            (swap! ray-positions conj position))
        (if blocked?
          (if see-all-tiles?
            visible-tile-color
            base-color)
          (do (when-not explored?
                (swap! explored-tile-corners assoc (mapv int position) true))
              visible-tile-color))))))

(comment
 (def ^:private count-rays? false)

 (def ray-positions (atom []))
 (def do-once (atom true))

 (count @ray-positions)
 2256
 (count (distinct @ray-positions))
 608
 (* 608 4)
 2432
 )

(defn- draw-world-map!
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (graphics/draw-tiled-map! graphics
                            (:world/tiled-map world)
                            (tile-color-setter
                             {:ray-blocked? (partial raycaster/blocked? world)
                              :explored-tile-corners (:world/explored-tile-corners world)
                              :light-position (graphics/position graphics)
                              :see-all-tiles? false
                              :explored-tile-color  [0.5 0.5 0.5 1]
                              :visible-tile-color   [1 1 1 1]
                              :invisible-tile-color [0 0 0 1]}))
  ctx)

(def ^:dbg-flag show-tile-grid? false)

(defn draw-tile-grid
  [{:keys [ctx/graphics]}]
  (when show-tile-grid?
    (let [[left-x _right-x bottom-y _top-y] (graphics/frustum graphics)]
      [[:draw/grid
        (int left-x)
        (int bottom-y)
        (inc (int (graphics/world-vp-width  graphics)))
        (+ 2 (int (graphics/world-vp-height graphics)))
        1
        1
        [1 1 1 0.8]]])))

(defn- highlight-mouseover-tile
  [{:keys [ctx/graphics
           ctx/world]}]
  (let [[x y] (mapv int (:graphics/world-mouse-position graphics))
        cell ((:world/grid world) [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air  [1 1 0 0.5]
          :none [1 0 0 0.5])]])))

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn- draw-cell-debug
  [{:keys [ctx/graphics
           ctx/world]}]
  (apply concat
         (for [[x y] (graphics/visible-tiles graphics)
               :let [cell ((:world/grid world) [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 [1 0 0 0.6]])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 [0 0 1 0.6]])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance ((:world/factions-iterations world) faction))]
                    [:draw/filled-rectangle x y 1 1 [ratio (- 1 ratio) ratio 0.6]]))))])))

(defn- draw-image
  [image
   {:keys [entity/body]}
   {:keys [ctx/graphics]}]
  [[:draw/texture-region
    (graphics/texture-region graphics image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])

(def ^:private hpbar-colors
  {:green     [0 0.8 0 1]
   :darkgreen [0 0.5 0 1]
   :yellow    [0.5 0.5 0 1]
   :red       [0.5 0 0 1]})

(defn- hpbar-color [ratio]
  (let [ratio (float ratio)
        color (cond
               (> ratio 0.75) :green
               (> ratio 0.5)  :darkgreen
               (> ratio 0.25) :yellow
               :else          :red)]
    (color hpbar-colors)))

(def ^:private borders-px 1)

(defn- draw-hpbar [world-unit-scale {:keys [body/position body/width body/height]} ratio]
  (let [[x y] position]
    (let [x (- x (/ width  2))
          y (+ y (/ height 2))
          height (* 5          world-unit-scale)
          border (* borders-px world-unit-scale)]
      [[:draw/filled-rectangle x y width height color/black]
       [:draw/filled-rectangle
        (+ x border)
        (+ y border)
        (- (* width ratio) (* 2 border))
        (- height          (* 2 border))
        (hpbar-color ratio)]])))

(def ^:private skill-image-radius-world-units
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(defn- draw-skill-image
  [texture-region entity [x y] action-counter-ratio]
  (let [radius skill-image-radius-world-units
        y (+ (float y)
             (float (/ (:body/height (:entity/body entity)) 2))
             (float 0.15))
        center [x (+ y radius)]]
    [[:draw/filled-circle center radius [1 1 1 0.125]]
     [:draw/sector
      center
      radius
      90 ; start-angle
      (* (float action-counter-ratio) 360) ; degree
      [1 1 1 0.5]]
     [:draw/texture-region texture-region [(- (float x) radius) y]]]))

(def effect-k->fn
  {:effects/target-all {:draw (fn [_
                                   {:keys [effect/source]}
                                   {:keys [ctx/world]}]
                                (let [{:keys [world/active-entities]} world
                                      source* @source]
                                  (for [target* (map deref (target-all/affected-targets active-entities world source*))]
                                    [:draw/line
                                     (:body/position (:entity/body source*)) #_(start-point source* target*)
                                     (:body/position (:entity/body target*))
                                     [1 0 0 0.5]])))}

   :effects/target-entity {:draw (fn [[_ {:keys [maxrange]}]
                                      {:keys [effect/source effect/target]}
                                      _ctx]
                                   (when target
                                     (let [body        (:entity/body @source)
                                           target-body (:entity/body @target)]
                                       [[:draw/line
                                         (target-entity/start-point body target-body)
                                         (target-entity/end-point body target-body maxrange)
                                         (if (target-entity/in-range? body target-body maxrange)
                                           [1 0 0 0.5]
                                           [1 1 0 0.5])]])))}})

(defn draw-effect [{k 0 :as component} effect-ctx ctx]
  (if-let [f (:draw (effect-k->fn k))]
    (f component effect-ctx ctx)
    nil))

(def ^:private render-layers
  (let [outline-alpha 0.4
        enemy-color [1 0 0 outline-alpha]
        friendly-color [0 1 0 outline-alpha]
        neutral-color [1 1 1 outline-alpha]
        mouseover-ellipse-width 5]
    [{:entity/mouseover?     (fn
                               [_
                                {:keys [entity/body
                                        entity/faction]}
                                {:keys [ctx/world]}]
                               (let [player @(:world/player-eid world)]
                                 [[:draw/with-line-width mouseover-ellipse-width
                                   [[:draw/ellipse
                                     (:body/position body)
                                     (/ (:body/width  body) 2)
                                     (/ (:body/height body) 2)
                                     (cond (= faction (faction/enemy (:entity/faction player)))
                                           enemy-color
                                           (= faction (:entity/faction player))
                                           friendly-color
                                           :else
                                           neutral-color)]]]]))

      :stunned               (fn [_ {:keys [entity/body]} _ctx]
                               [[:draw/circle
                                 (:body/position body)
                                 0.5
                                 [1 1 1 0.6]]])

      :player-item-on-cursor (fn
                               [{:keys [item]}
                                entity
                                {:keys [ctx/graphics
                                        ctx/input
                                        ctx/stage]}]
                               ; TODO do not draw here, only at UI view
                               ; then graphics can draw world without stage/input
                               (when (player-item-on-cursor/world-item? (ui/mouseover-actor stage (input/mouse-position input)))
                                 [[:draw/texture-region
                                   (graphics/texture-region graphics (:entity/image item))
                                   (player-item-on-cursor/item-place-position (:graphics/world-mouse-position graphics)
                                                                              entity)
                                   {:center? true}]]))}
     {:entity/clickable      (fn
                               [{:keys [text]}
                                {:keys [entity/body
                                        entity/mouseover?]}
                                _ctx]
                               (when (and mouseover? text)
                                 (let [[x y] (:body/position body)]
                                   [[:draw/text {:text text
                                                 :x x
                                                 :y (+ y (/ (:body/height body) 2))
                                                 :up? true}]])))

      :entity/animation      (fn [animation entity ctx]
                               (draw-image (animation/current-frame animation)
                                           entity
                                           ctx))

      :entity/image          draw-image

      :entity/line-render    (fn [{:keys [thick? end color]} {:keys [entity/body]} _ctx]
                               (let [position (:body/position body)]
                                 (if thick?
                                   [[:draw/with-line-width
                                     4
                                     [[:draw/line position end color]]]]
                                   [[:draw/line position end color]])))}

     {:npc-sleeping          (fn [_ {:keys [entity/body]} _ctx]
                               (let [[x y] (:body/position body)]
                                 [[:draw/text {:text "zzz"
                                               :x x
                                               :y (+ y (/ (:body/height body) 2))
                                               :up? true}]]))

      :entity/temp-modifier  (fn [_ entity _ctx]
                               [[:draw/filled-circle
                                 (:body/position (:entity/body entity))
                                 0.5
                                 [0.5 0.5 0.5 0.4]]])

      :entity/string-effect  (fn [{:keys [text]} entity {:keys [ctx/graphics]}]
                               (let [[x y] (:body/position (:entity/body entity))]
                                 [[:draw/text {:text text
                                               :x x
                                               :y (+ y
                                                     (/ (:body/height (:entity/body entity)) 2)
                                                     (* 5 (:graphics/world-unit-scale graphics)))
                                               :scale 2
                                               :up? true}]]))}

     {:entity/stats          (fn [_ entity {:keys [ctx/graphics]}]
                               (let [ratio (val-max/ratio (stats/get-hitpoints (:entity/stats entity)))]
                                 (when (or (< ratio 1) (:entity/mouseover? entity))
                                   (draw-hpbar (:graphics/world-unit-scale graphics)
                                               (:entity/body entity)
                                               ratio))))
      :active-skill          (fn
                               [{:keys [skill effect-ctx counter]}
                                entity
                                {:keys [ctx/graphics
                                        ctx/world]
                                 :as ctx}]
                               (let [{:keys [entity/image skill/effects]} skill]
                                 (concat (draw-skill-image (graphics/texture-region graphics image)
                                                           entity
                                                           (:body/position (:entity/body entity))
                                                           (timer/ratio (:world/elapsed-time world) counter))
                                         (mapcat #(draw-effect % effect-ctx ctx)  ; update-effect-ctx here too ?
                                                 effects))))}]))

(def ^:dbg-flag show-body-bounds? false)

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color]]))

(defn- draw-entity
  [{:keys [ctx/graphics]
    :as ctx}
   entity render-layer]
  (try (do
        (when show-body-bounds?
          (graphics/draw! graphics (draw-body-rect (:entity/body entity)
                                                   (if (:body/collides? (:entity/body entity))
                                                     color/white
                                                     color/gray))))
        (doseq [[k v] entity
                :let [draw-fn (get render-layer k)]
                :when draw-fn]
          (graphics/draw! graphics (draw-fn v entity ctx))))
       (catch Throwable t
         (graphics/draw! graphics (draw-body-rect (:entity/body entity) color/red))
         (throwable/pretty-pst t))))

(defn draw-entities
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [entities (map deref (:world/active-entities world))
        player @(:world/player-eid world)
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? world player entity)))]
    (doseq [[z-order entities] (utils/sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                                    first
                                                    (:world/render-z-order world))
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))

(defn- draw-on-world-viewport!
  [{:keys [ctx/graphics]
    :as ctx}]
  (graphics/draw-on-world-vp! graphics
                              (fn []
                                (doseq [f [draw-tile-grid
                                           draw-cell-debug
                                           draw-entities
                                           #_moon.application.render.draw-on-world-viewport.geom-test/do!
                                           highlight-mouseover-tile]]
                                  (graphics/draw! graphics (f ctx)))))
  ctx)

(defn- player-effect-ctx [mouseover-eid world-mouse-position player-eid]
  (let [target-position (or (and mouseover-eid
                                 (:body/position (:entity/body @mouseover-eid)))
                            world-mouse-position)]
    {:effect/source player-eid
     :effect/target mouseover-eid
     :effect/target-position target-position
     :effect/target-direction (v/direction (:body/position (:entity/body @player-eid))
                                           target-position)}))

(defn- interaction-state
  [stage
   world-mouse-position
   mouseover-eid
   player-eid
   mouseover-actor]
  (cond
   mouseover-actor
   [:interaction-state/mouseover-actor (ui/actor-information stage mouseover-actor)]

   (and mouseover-eid
        (:entity/clickable @mouseover-eid))
   [:interaction-state/clickable-mouseover-eid
    {:clicked-eid mouseover-eid
     :in-click-range? (< (body/distance (:entity/body @player-eid)
                                        (:entity/body @mouseover-eid))
                         (:entity/click-distance-tiles @player-eid))}]

   :else
   (if-let [skill-id (ui/action-bar-selected-skill stage)]
     (let [entity @player-eid
           skill (skill-id (:entity/skills entity))
           effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
           state (skill/usable-state skill entity effect-ctx)]
       (if (= state :usable)
         [:interaction-state.skill/usable [skill effect-ctx]]
         [:interaction-state.skill/not-usable state]))
     [:interaction-state/no-skill-selected])))

(defn- assoc-interaction-state
  [{:keys [ctx/graphics
           ctx/input
           ctx/stage
           ctx/world]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       (:graphics/world-mouse-position graphics)
                                                       (:world/mouseover-eid world)
                                                       (:world/player-eid    world)
                                                       (ui/mouseover-actor stage (input/mouse-position input)))))

(defn- player-idle->cursor [player-eid {:keys [ctx/interaction-state]}]
  (let [[k params] interaction-state]
    (case k
      :interaction-state/mouseover-actor
      (let [[actor-type params] params
            inventory-cell-with-item? (and (= actor-type :mouseover-actor/inventory-cell)
                                           (let [inventory-slot params]
                                             (get-in (:entity/inventory @player-eid) inventory-slot)))]
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

(let [fn-map {:active-skill :cursors/sandclock
              :player-dead :cursors/black-x
              :player-idle player-idle->cursor
              :player-item-on-cursor :cursors/hand-grab
              :player-moving :cursors/walking
              :stunned :cursors/denied}]
  (defn state->cursor [[k v] eid ctx]
    (let [->cursor (k fn-map)]
      (if (keyword? ->cursor)
        ->cursor
        (->cursor eid ctx)))))

(defn- set-cursor
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state->cursor [state-k (state-k entity)] eid ctx)]
    (graphics/set-cursor! graphics cursor-key))
  ctx)

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
             (ui/inventory-window-visible? stage)
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

(let [fn-map {:player-idle           (fn
                                       [player-eid
                                        {:keys [ctx/input
                                                ctx/interaction-state
                                                ctx/stage] :as ctx}]
                                       (if-let [movement-vector (input/player-movement-vector input)]
                                         [[:tx/event player-eid :movement-input movement-vector]]
                                         (when (input/button-just-pressed? input Input$Buttons/LEFT)
                                           (interaction-state->txs interaction-state
                                                                   stage
                                                                   player-eid))))

              :player-item-on-cursor (fn
                                       [eid {:keys [ctx/input
                                                    ctx/stage]}]
                                       (let [mouseover-actor (ui/mouseover-actor stage (input/mouse-position input))]
                                         (when (and (input/button-just-pressed? input Input$Buttons/LEFT)
                                                    (player-item-on-cursor/world-item? mouseover-actor))
                                           [[:tx/event eid :drop-item]])))

              :player-moving         (let [speed (fn [{:keys [entity/stats]}]
                                                   (or (stats/get-stat-value stats :stats/movement-speed)
                                                       0))]
                                       (fn [eid {:keys [ctx/input]}]
                                         (if-let [movement-vector (input/player-movement-vector input)]
                                           [[:tx/assoc eid :entity/movement {:direction movement-vector
                                                                             :speed (speed @eid)}]]
                                           [[:tx/event eid :no-movement-input]])))}]
  (defn- state->handle-input [[k v] eid ctx]
    (if-let [f (k fn-map)]
      (f eid ctx)
      nil)))

(defn- player-state-handle-input
  [{:keys [ctx/world]
    :as ctx}]
  (let [eid (:world/player-eid world)
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state->handle-input [state-k (state-k entity)] eid ctx)]
    (ctx/handle! ctx txs))
  ctx)

(defn- dissoc-interaction-state [ctx]
  (dissoc ctx :ctx/interaction-state))

(def pausing? true)

(def state->pause-game? {:stunned false
                         :player-moving false
                         :player-item-on-cursor true
                         :player-idle true
                         :player-dead true
                         :active-skill false})

(defn- assoc-paused
  [{:keys [ctx/input
           ctx/world]
    :as ctx}]
  (assoc-in ctx [:ctx/world :world/paused?]
            (or #_error
                (and pausing?
                     (state->pause-game? (:state (:entity/fsm @(:world/player-eid world))))
                     (not (or (input/key-just-pressed? input (:unpause-once input/controls))
                              (input/key-pressed? input (:unpause-continously input/controls))))))))

(defn- update-world-time
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (if (:world/paused? (:ctx/world ctx))
    ctx
    (update ctx :ctx/world world/update-time (graphics/delta-time graphics))))

(defn- update-potential-fields
  [{:keys [ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do
     (world/update-potential-fields! world)
     ctx)))

(defn- tick-entities
  [{:keys [ctx/skin
           ctx/stage
           ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do (try
         (ctx/handle! ctx (world/tick-entities! world))
         (catch Throwable t
           (throwable/pretty-pst t)
           (ui/show-error-window! stage skin t)))
        ctx)))

(defn- remove-destroyed-entities
  [{:keys [ctx/world]
    :as ctx}]
  (ctx/handle! ctx (world/remove-destroyed-entities! world))
  ctx)

(def zoom-speed 0.025)

(defn- window-camera-controls
  [{:keys [ctx/input
           ctx/graphics
           ctx/stage]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in input/controls))
    (graphics/change-zoom! graphics zoom-speed))

  (when (input/key-pressed? input (:zoom-out input/controls))
    (graphics/change-zoom! graphics (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key input/controls))
    (ui/close-all-windows! stage))

  (when (input/key-just-pressed? input (:toggle-inventory input/controls))
    (ui/toggle-inventory-visible! stage))

  (when (input/key-just-pressed? input (:toggle-entity-info input/controls))
    (ui/toggle-entity-info-window! stage))
  ctx)

(defn- render-stage
  [{:keys [^Stage ctx/stage]
    :as ctx}]
  (set! (.ctx stage) ctx)
  (.act  stage)
  (.draw stage)
  (.ctx  stage))

(defn do! [ctx]
  (-> ctx
      get-stage-ctx
      validate
      update-mouse
      update-mouseover-eid
      check-open-debug
      assoc-active-entities
      set-camera-on-player
      clear-screen!
      draw-world-map!
      draw-on-world-viewport!
      assoc-interaction-state
      set-cursor
      player-state-handle-input
      dissoc-interaction-state
      assoc-paused
      update-world-time
      update-potential-fields
      tick-entities
      remove-destroyed-entities
      window-camera-controls
      render-stage
      validate))
