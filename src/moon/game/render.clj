(ns moon.game.render
  (:require [clojure.gdx.maps.tiled.renderer :as tiled-map-renderer]
            [clojure.gdx.scene2d.stage]
            [clojure.gdx.scene2d.ui.widget-group]
            [clojure.gdx.shape-drawer]
            [clojure.gdx.viewport]
            [clojure.graphics :as graphics]
            [clojure.graphics.batch :as batch]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.graphics.viewport :as viewport]
            [clojure.input :as input]
            [clojure.math.vector2 :as v]
            [clojure.scene2d.actor :as actor]
            [clojure.scene2d.group :as group]
            [clojure.scene2d.stage :as stage]
            [moon.action-bar :as action-bar]
            [moon.body :as body]
            [moon.ctx :as ctx]
            [moon.content-grid :as content-grid]
            [moon.draws :as draws]
            [moon.entity :as entity]
            [moon.grid :as grid]
            [moon.if-not-paused.update-potential-fields]
            [moon.order :as order]
            [moon.raycaster :as raycaster]
            [moon.skill :as skill]
            [moon.state :as state]
            [moon.throwable :as throwable]
            [moon.txs :as txs]))

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
    :light-position (camera/position (viewport/camera world-viewport))
    :see-all-tiles? false
    :explored-tile-color  (:colors/explored-tile colors)
    :visible-tile-color   (:colors/visible-tile colors)
    :invisible-tile-color (:colors/invisible-tile colors)}))

(defn draw-tiled-map!
  [{:keys [ctx/batch
           ctx/tiled-map
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}]
  (tiled-map-renderer/draw! batch
                            world-unit-scale
                            (viewport/camera world-viewport)
                            tiled-map
                            (tile-color-setter ctx))
  ctx)

(defn draw-on-world-viewport!
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (batch/set-color! batch 1 1 1 1)
  ;
  (batch/set-projection-matrix! batch (camera/combined (viewport/camera world-viewport)))
  (batch/begin! batch)
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f draw-fns]
      (draws/handle ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (batch/end! batch)
  ctx)

(def ^:dbg-flag show-potential-field-colors? false) ; :good, :evil
(def ^:dbg-flag show-cell-entities? false)
(def ^:dbg-flag show-cell-occupied? false)

(defn draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/factions-iterations
           ctx/world-viewport]}]
  (apply concat
         (for [[x y] (camera/visible-tiles (viewport/camera world-viewport))
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


(def ^:private render-layers ; TODO move external - simple TODO/checklist / state pass
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

(def ^:dbg-flag show-body-bounds? false) ; TODO same here?

(defn- draw-body-rect [{:keys [body/position body/width body/height]} color-float-bits]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    [[:draw/rectangle x y width height color-float-bits]]))

(defn- draw-entity
  [{:keys [ctx/colors] :as ctx} entity render-layer]
  (try (do
        (when show-body-bounds?
          (draws/handle ctx (draw-body-rect (:entity/body entity)
                                             (if (:body/collides? (:entity/body entity))
                                               (:colors/debug-body-outline-collides colors)
                                               (:colors/debug-body-outline colors)))))
        (doseq [[k v] entity
                :when (get render-layer k)]
          (draws/handle ctx (entity/render [k v] entity ctx))))
       (catch Throwable t
         (draws/handle ctx (draw-body-rect (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
         (throwable/pretty-pst t))))

(defn draw-entities
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
    (doseq [[z-order entities] (order/sort-by-order (group-by (comp :body/z-order :entity/body) entities)
                                                    first
                                                    render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (draw-entity ctx entity render-layer))))

(defn draw-tile-grid
  [{:keys [ctx/world-viewport]}]
  (let [[left-x _right-x bottom-y _top-y] (camera/frustum (viewport/camera world-viewport))]
    [[:draw/grid
      (int left-x)
      (int bottom-y)
      (inc (int (viewport/world-width  world-viewport)))
      (+ 2 (int (viewport/world-height world-viewport)))
      1
      1
      [1 1 1 0.8]]]))

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

(defn- mouseover-actor-info [actor]
  (let [inventory-slot (and (actor/parent actor)
                            (= "inventory-cell" (actor/name (actor/parent actor)))
                            (actor/user-object (actor/parent actor)))]
    (cond
     inventory-slot            [:mouseover-actor/inventory-cell inventory-slot]
     (actor/window-title-bar? actor) [:mouseover-actor/window-title-bar]
     (actor/button?           actor) [:mouseover-actor/button]
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

(defn- assoc-interaction-state
  [{:keys [ctx/input
           ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (assoc ctx :ctx/interaction-state (interaction-state stage
                                                       world-mouse-position
                                                       mouseover-eid
                                                       player-eid
                                                       (stage/mouseover-actor stage (input/mouse-position input)))))

(defn- set-cursor
  [{:keys [ctx/cursors
           ctx/graphics
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-key (state/cursor [state-k (state-k entity)] eid ctx)]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! graphics (get cursors cursor-key)))
  ctx)

(defn- player-state-handle-input
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (state/handle-input [state-k (state-k entity)] eid ctx)]
    (txs/handle! ctx txs))
  ctx)

(defn update-player-state [ctx]
  (-> ctx
      assoc-interaction-state
      set-cursor
      player-state-handle-input
      (dissoc :ctx/interaction-state)))

(def pausing? true) ; TODO FIXME

(defn assoc-paused
  [{:keys [ctx/controls
           ctx/input
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state/pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (input/key-just-pressed? input (:unpause-once controls))
                           (input/key-pressed? input (:unpause-continously controls))))))))

(defn if-not-paused
  [{:keys [ctx/paused?]
    :as ctx}
   fns]
  (if paused?
    ctx
    (reduce (fn [ctx [f & params]]
              (apply f ctx params))
            ctx
            fns)))

(defn remove-destroyed-entities
  [ctx]
  (txs/handle! ctx (mapcat
                    (fn [eid]
                      (cons
                       [:tx/unregister-eid eid]
                       (mapcat (fn [[k v]]
                                 (entity/destroy [k v] eid))
                               @eid)))
                    (filter (comp :entity/destroyed? deref)
                            (vals @(:ctx/entity-ids ctx)))))
  ctx)

(def zoom-speed 0.025) ; TODO FIXME pull out

; TODO Stage handlers somehow?
(defn window-camera-controls
  [{:keys [ctx/controls
           ctx/input
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (input/key-pressed? input (:zoom-in controls))
    (camera/inc-zoom! (viewport/camera world-viewport) zoom-speed))

  (when (input/key-pressed? input (:zoom-out controls))
    (camera/inc-zoom! (viewport/camera world-viewport) (- zoom-speed)))

  (when (input/key-just-pressed? input (:close-windows-key controls))
    (->> (stage/find-actor stage "moon.ui.windows")
         group/children
         (run! #(actor/set-visible! % false))))

  (when (input/key-just-pressed? input (:toggle-inventory controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.inventory")
        actor/toggle-visible!))

  (when (input/key-just-pressed? input (:toggle-entity-info controls))
    (-> stage
        (stage/find-actor "moon.ui.windows.entity-info")
        actor/toggle-visible!))
  ctx)

(defn render-stage!
  [{:keys [ctx/stage] :as ctx}]
  (stage/set-ctx! stage ctx)
  (stage/act!  stage)
  (stage/draw! stage)
  (stage/ctx stage))

(defn update-time
  [{:keys [ctx/graphics
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (graphics/delta-time graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn tick-entities!
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
   (txs/handle! ctx (mapcat (fn [eid]
                              (mapcat (fn [[k v]]
                                        (try (entity/tick [k v] eid ctx)
                                             (catch Throwable t
                                               (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                                      @eid))
                            active-entities))
   (catch Throwable t
     (throwable/pretty-pst t)
     (stage/add-actor! stage
                       (actor/create
                        {:type :ui/error-window
                         :skin skin
                         :throwable t}))))
  ctx)

(comment
 (= (tick-entities! {:ctx/active-entities [(atom {:firstk :foo
                                                    :secondk :bar})
                                             (atom {:firstk2 :foo2
                                                    :secondk2 :bar2})]}
                    {:firstk (fn [v eid world]
                               [[:foo/bar]])
                     :secondk (fn [v eid world]
                                [[:foo/barz]
                                 [:asdf]])
                     :firstk2 (fn [v eid world]
                                nil)
                     :secondk2 (fn [v eid world]
                                 [[:asdf2] [:asdf3]])})
    [[:foo/bar]
     [:foo/barz]
     [:asdf]
     [:asdf2]
     [:asdf3]])
 )

(defn update-potential-fields
  [{:keys [ctx/active-entities
           ctx/factions-iterations
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (moon.if-not-paused.update-potential-fields/tick!
     potential-field-cache
     grid
     faction
     active-entities
     max-iterations))
  ctx)

(defn do! [ctx]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          (concat
           [
            [(fn
               [{:keys [ctx/stage]
                 :as ctx}]
               (or (stage/ctx stage)
                   ctx))] ; first render stage does not have ctx set.

            [(fn [ctx]
               (ctx/validate ctx)
               ctx)]

            [(fn
               [{:keys [ctx/input
                        ctx/ui-viewport
                        ctx/world-viewport]
                 :as ctx}]
               (let [mp (input/mouse-position input)]
                 (-> ctx
                     (assoc :ctx/world-mouse-position (viewport/unproject world-viewport mp))
                     (assoc :ctx/ui-mouse-position (viewport/unproject ui-viewport mp)))))]

            [(fn
               [{:keys [ctx/input
                        ctx/mouseover-eid
                        ctx/stage
                        ctx/player-eid
                        ctx/grid
                        ctx/raycaster
                        ctx/render-z-order
                        ctx/world-mouse-position]
                 :as ctx}]
               (let [mouseover-actor (stage/mouseover-actor stage (input/mouse-position input))
                     position world-mouse-position
                     new-eid (if mouseover-actor
                               nil
                               (let [player @player-eid
                                     hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                                  (grid/point->entities grid position))]
                                 (->> render-z-order
                                      (order/sort-by-order hits #(:body/z-order (:entity/body @%)))
                                      reverse
                                      (filter #(raycaster/line-of-sight? raycaster player @%))
                                      first)))]
                 (when mouseover-eid
                   (swap! mouseover-eid dissoc :entity/mouseover?))
                 (when new-eid
                   (swap! new-eid assoc :entity/mouseover? true))
                 (assoc ctx :ctx/mouseover-eid new-eid)))]

            [(fn
               [{:keys [ctx/controls
                        ctx/input
                        ctx/mouseover-eid
                        ctx/skin
                        ctx/stage
                        ctx/grid
                        ctx/world-mouse-position]
                 :as ctx}]
               (when (input/button-just-pressed? input (:open-debug-button controls))
                 (let [data (or (and mouseover-eid @mouseover-eid)
                                @(grid (mapv int world-mouse-position)))]
                   (stage/add-actor! stage
                                     (actor/create
                                      {:type :ui/data-viewer-window
                                       :title "Data View"
                                       :data data
                                       :width 500
                                       :height 500
                                       :skin skin}))))
               ctx)]

            [(fn
               [{:keys [ctx/player-eid
                        ctx/content-grid]
                 :as ctx}]
               (assoc ctx :ctx/active-entities
                      (content-grid/active-entities content-grid @player-eid)))]

            [(fn
               [{:keys [ctx/player-eid
                        ctx/world-viewport]
                 :as ctx}]
               (camera/set-position! (viewport/camera world-viewport)
                                     (:body/position (:entity/body @player-eid)))
               ctx)]

            [(fn [ctx]
               (graphics/clear! (:ctx/graphics ctx) 0 0 0 0)
               ctx)]

            ]
           [
            [draw-tiled-map!]
            [draw-on-world-viewport! [
                                      #_draw-tile-grid
                                      draw-cell-debug
                                      draw-entities
                                      #_moon.geom-test
                                      highlight-mouseover-tile
                                      ]]
            [update-player-state]
            [assoc-paused]
            [if-not-paused [
                            [update-time]
                            [update-potential-fields]
                            [tick-entities!]
                            ]]
            [remove-destroyed-entities]
            [window-camera-controls]
            [render-stage!]
            [(fn [ctx]
               (ctx/validate ctx)
               ctx)]
            ])))
