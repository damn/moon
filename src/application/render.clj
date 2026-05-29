(ns application.render
  (:require [clojure.core-ext :refer [sort-by-order]]
            [clojure.math.vector2 :as v]
            [game.ctx :as ctx]
            [game.entity :as entity]
            [game.skill :as skill]
            [game.state :as state]
            [gdx.application :as app]
            [gdx.graphics :as graphics]
            [gdx.graphics.color :as color]
            [gdx.graphics.orthographic-camera :as camera]
            [gdx.graphics.shape-drawer :as shape-drawer]
            [gdx.scenes.scene2d.actor :as actor]
            [gdx.scenes.scene2d.group :as group]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.scenes.scene2d.ui :as ui]
            [gdx.scenes.scene2d.ui.action-bar :as action-bar]
            [gdx.scenes.scene2d.ui.data-viewer-window :as data-viewer-window]
            [gdx.tiled-map-renderer :as tiled-map-renderer]
            [gdx.utils.viewport.viewport :as viewport]
            [malli.core :as m]
            [malli.utils :as mu]
            [moon.body :as body]
            [moon.content-grid :as content-grid]
            [moon.grid :as grid]
            [moon.raycaster :as raycaster]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window])
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch)))

(defn- tile-color-setter*
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

(defn- tile-color-setter
  [{:keys [ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/world-viewport]}]
  (tile-color-setter*
   {:ray-blocked? (partial raycaster/blocked? raycaster)
    :explored-tile-corners explored-tile-corners
    :light-position (camera/position (:viewport/camera world-viewport))
    :see-all-tiles? false
    :explored-tile-color  (:colors/explored-tile colors)
    :visible-tile-color   (:colors/visible-tile colors)
    :invisible-tile-color (:colors/invisible-tile colors)}))


; Try only to be used here
; then make ctx protocols
; then see what's used together
; and data abstraction
(def schema
  (m/schema
   [:map {:closed true}
    ; Input, Audio, Files, Graphics
    [:ctx/app :some] ; ~~ only used in this ns ~~ ✅

    ; Audio, Files
    [:ctx/audio :some] ; ~~ only used in this ns ~~ ✅

    ; Graphics
    [:ctx/batch :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/cursors :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/default-font :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/unit-scale :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/world-unit-scale :some] ; ~~ only used in this ns ~~ ✅ -- accessed through ctx/world-unit-scale protocol
    [:ctx/world-viewport :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/shape-drawer :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/shape-drawer-texture :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/textures :some] ; 💥⚠️

    ; UI
    [:ctx/skin :some] ; 💥⚠️
    [:ctx/stage :some] ; 💥⚠️

    ; Frame
    [:ctx/active-entities :any] ; 💥⚠️
    [:ctx/delta-time :any] ; 💥⚠️
    [:ctx/mouseover-eid :any]
    [:ctx/ui-mouse-position :any]
    [:ctx/world-mouse-position :any]

    ; Constants
    [:ctx/colors :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/render-z-order :some]
    [:ctx/z-orders :some]

    ; Game
    ; The 'game' could be a separate library with no libgdx dependencies?
    [:ctx/content-grid :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]
    [:ctx/db :some] ; used here & @ schema....
    [:ctx/elapsed-time :some] ; effect, info, entity, state
    [:ctx/paused? :some] ; only here ✅
    [:ctx/player-eid :some] ; used @ info & entity ⚠️
    ]))

(defn world-viewport-width
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-width world-viewport))

(defn world-viewport-height
  [{:keys [ctx/world-viewport]}]
  (:viewport/world-height world-viewport))
(def zoom-speed 0.025)

(defn visible-tiles [{:keys [ctx/world-viewport]}]
  (camera/visible-tiles (:viewport/camera world-viewport)))

(defn- mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/parent actor)
                            (= "inventory-cell" (actor/name (actor/parent actor)))
                            (actor/user-object (actor/parent actor)))]
    (cond
     inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
     (ui/window-title-bar? actor) [:mouseover-actor/window-title-bar]
     (ui/button? actor)           [:mouseover-actor/button]
     :else                     [:mouseover-actor/unspecified])))

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
   [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

   (and mouseover-eid
        (:entity/clickable @mouseover-eid))
   [:interaction-state/clickable-mouseover-eid
    {:clicked-eid mouseover-eid
     :in-click-range? (< (body/distance (:entity/body @player-eid)
                                        (:entity/body @mouseover-eid))
                         (:entity/click-distance-tiles @player-eid))}]

   :else
   (if-let [skill-id (-> stage
                         (stage/find-actor "moon.ui.action-bar")
                         action-bar/selected-skill)]
     (let [entity @player-eid
           skill (skill-id (:entity/skills entity))
           effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
           state (skill/usable-state skill entity effect-ctx)]
       (if (= state :usable)
         [:interaction-state.skill/usable [skill effect-ctx]]
         [:interaction-state.skill/not-usable state]))
     [:interaction-state/no-skill-selected])))








(def pausing? true)

(defn delta-time
  [{:keys [ctx/app]}]
  (graphics/delta-time (app/graphics app)))


(defn update-time
  [{:keys [ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (delta-time ctx) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn update-potential-fields!
  [{:keys [ctx/active-entities
           ctx/factions-iterations
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (grid/tick! grid
                potential-field-cache
                faction
                active-entities
                max-iterations))
  ctx)

(defn tick-entities!
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
   (ctx/do! ctx (mapcat (fn [eid]
                          (mapcat (fn [[k v]]
                                    (try (entity/tick [k v] eid ctx)
                                         (catch Throwable t
                                           (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                                  @eid))
                        active-entities))
   (catch Throwable t
     (throwable/pretty-pst t)
     (stage/add-actor! stage
                       (error-window/create
                        {:skin skin
                         :throwable t}))))
  ctx)







(defn camera-frustum [{:keys [ctx/world-viewport]}]
  (camera/frustum (:viewport/camera world-viewport)))

(defn draw-tile-grid
  [ctx]
  (let [[left-x _right-x bottom-y _top-y] (camera-frustum ctx)]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (world-viewport-width ctx)))
      (+ 2 (int (world-viewport-height ctx)))
      1
      1
      (color/float-bits [1 1 1 0.8])]]))

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/factions-iterations]
    :as ctx}]
  (apply concat
         (for [[x y] (visible-tiles ctx)
               :let [cell (grid [x y])]
               :when cell
               :let [cell* @cell]]
           [(when (and show-cell-entities? (seq (:entities cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-entities colors)])
            (when (and show-cell-occupied? (seq (:occupied cell*)))
              [:draw/filled-rectangle x y 1 1 (:colors/debug-cell-occupied colors)])
            (when-let [faction show-potential-field-colors?]
              (let [{:keys [distance]} (faction cell*)]
                (when distance
                  (let [ratio (/ distance (factions-iterations faction))]
                    [:draw/filled-rectangle x y 1 1 ((:colors/debug-potential-field colors) ratio)]))))])))

(def ^:private render-layers
  [#{:entity/mouseover?
     :stunned
     :player-item-on-cursor}
   #{:entity/clickable
     :entity/animation
     :entity/image
     :entity/line-render}
   #{:npc-sleeping
     :entity/temp-modifier
     :entity/string-effect}
   #{:entity/stats
     :active-skill}])

(def ^:dbg-flag show-body-bounds? false)

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color-float-bits]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color-float-bits]]))

(defn- draw-entity
  [{:keys [ctx/colors] :as ctx} entity render-layer]
  (try (do
        (when show-body-bounds?
          (ctx/draw! ctx (draw-body-rect (:entity/body entity)
                                         (if (:body/collides? (:entity/body entity))
                                           (:colors/debug-body-outline-collides colors)
                                           (:colors/debug-body-outline colors)))))
        (doseq [[k v] entity
                :when (get render-layer k)]
          (ctx/draw! ctx (entity/render [k v] entity ctx))))
       (catch Throwable t
         (ctx/draw! ctx (draw-body-rect (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
         (throwable/pretty-pst t))))

(defn draw-entities!
  [{:keys [ctx/active-entities
           ctx/player-eid
           ctx/raycaster
           ctx/render-z-order]
    :as ctx}]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (raycaster/line-of-sight? raycaster player entity)))]
    (doseq [[z-order entities] (sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                              first
                                              render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))

(defn highlight-mouseover-tile
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell (grid [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air  (:colors/mouseover-tile-air colors)
          :none (:colors/mouseover-tile-none colors))]])))




(defn- get-stage-ctx
  [{:keys [ctx/stage]
    :as ctx}]
  (or (:stage/ctx stage)
      ctx)) ; first render stage does not have ctx set.

(defn validate [ctx]
  (mu/validate-humanize schema ctx)
  ctx)

(defn update-mouse-positions
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (ctx/mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (viewport/unproject mp))))))

(defn update-mouseover-eid
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/grid
           ctx/raycaster
           ctx/render-z-order
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor (stage/mouseover-actor stage (ctx/mouse-position ctx))
        position world-mouse-position
        new-eid (if mouseover-actor
                  nil
                  (let [player @player-eid
                        hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                     (grid/point->entities grid position))]
                    (->> render-z-order
                         (sort-by-order hits #(:body/z-order (:entity/body @%)))
                         reverse
                         (filter #(raycaster/line-of-sight? raycaster player @%))
                         first)))]
    (when mouseover-eid
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc ctx :ctx/mouseover-eid new-eid)))

(defn check-debug-viewer
  [{:keys [ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (ctx/button-just-pressed? ctx (:open-debug-button controls))
    (let [data (or (and mouseover-eid @mouseover-eid)
                   @(grid (mapv int world-mouse-position)))]
      (stage/add-actor! stage
                        (data-viewer-window/create
                         {:title "Data View"
                          :data data
                          :width 500
                          :height 500
                          :skin skin}))))
  ctx)

(defn set-active-entities
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (content-grid/active-entities content-grid @player-eid)))

(defn set-camera-position!
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera/set-position! (:viewport/camera world-viewport)
                        (:body/position (:entity/body @player-eid)))
  ctx)

(defn clear-screen!
  [{:keys [ctx/app] :as ctx}]
  (graphics/clear! (app/graphics app) 0 0 0 0)
  ctx)

(defn draw-tiled-map!
  [{:keys [ctx/batch
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  (tiled-map-renderer/draw! batch
                            world-unit-scale
                            (:viewport/camera world-viewport)
                            tiled-map
                            (tile-color-setter ctx))
  ctx)

(defn draw-on-world-viewport!
  [{:keys [^SpriteBatch ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (.setColor batch 1 1 1 1)
  ;
  (.setProjectionMatrix batch (camera/combined (:viewport/camera world-viewport)))
  (.begin batch)
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f [
               #_draw-tile-grid
               draw-cell-debug
               draw-entities!
               #_moon.geom-test
               highlight-mouseover-tile
               ]]
      (ctx/draw! ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (.end batch)
  ctx)

(defn assoc-interaction-state
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       world-mouse-position
                                                       mouseover-eid
                                                       player-eid
                                                       (stage/mouseover-actor stage (ctx/mouse-position ctx)))))

(defn set-cursor!
  [{:keys [ctx/app
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state/cursor [state-k (state-k entity)] eid ctx)]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! (app/graphics app) (get cursors cursor-key)))
  ctx)

(defn handle-player-state-input!
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state/handle-input [state-k (state-k entity)] eid ctx)]
    (ctx/do! ctx txs))
  ctx)

(defn dissoc-interaction-state [ctx]
  (dissoc ctx :ctx/interaction-state))

(defn assoc-paused
  [{:keys [ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state/pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (ctx/key-just-pressed? ctx (:unpause-once controls))
                           (ctx/key-pressed? ctx (:unpause-continously controls))))))))

(defn if-not-paused-steps
  [{:keys [ctx/paused?]
    :as ctx}]
  (if paused?
    ctx
    (-> ctx
        update-time
        update-potential-fields!
        tick-entities!)))

(defn remove-destroyed-entities!
  [ctx]
  (ctx/do! ctx (mapcat
                (fn [eid]
                  (cons
                   [:tx/unregister-eid eid]
                   (mapcat (fn [[k v]]
                             (entity/destroy [k v] eid))
                           @eid)))
                (filter (comp :entity/destroyed? deref)
                        (vals @(:ctx/entity-ids ctx)))))
  ctx)

(defn window-camera-controls
  [{:keys [ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (ctx/key-pressed? ctx (:zoom-in controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (ctx/key-pressed? ctx (:zoom-out controls))
    (camera/inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (ctx/key-just-pressed? ctx (:close-windows-key controls))
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (ctx/key-just-pressed? ctx (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (ctx/key-just-pressed? ctx (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)

(defn update-draw-stage
  [{:keys [ctx/stage] :as ctx}]
  (stage/set-ctx! stage ctx)
  (stage/act!  stage)
  (stage/draw! stage)
  (:stage/ctx stage))

(defn do! [ctx]
  (-> ctx
      get-stage-ctx
      validate
      update-mouse-positions
      update-mouseover-eid
      check-debug-viewer
      set-active-entities
      set-camera-position!
      clear-screen!
      draw-tiled-map!
      draw-on-world-viewport!
      assoc-interaction-state
      set-cursor!
      handle-player-state-input!
      dissoc-interaction-state
      assoc-paused
      if-not-paused-steps
      remove-destroyed-entities!
      window-camera-controls
      update-draw-stage
      validate))
