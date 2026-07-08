(ns clojure.moon.render
  (:require [clojure.app-schema :refer [schema]]
            [clojure.content-grid.active-entities :as active-entities]
            [clojure.body-distance :as distance]
            [clojure.body-draw-rectangle :as draw-rectangle]
            [clojure.batch :as batch]
            [clojure.color-setter :refer [tile-color-setter*]]
            [clojure.ctx-button-just-pressed :refer [button-just-pressed?]]
            [clojure.ctx-do :refer [do!]]
            [clojure.data-viewer-window :as data-viewer-window]
            [clojure.draw :refer [draw!]]
            [clojure.draw-component :refer [draw-component]]
            [clojure.error-window :as error-window]
            [clojure.factions-iterations :refer [factions-iterations]]
            [clojure.gdx-draw-tiled-map :as draw-tiled-map]
            [clojure.graphics :as graphics]
            [clojure.graphics-shape-drawer :as shape-drawer]
            [clojure.grid-update-potential-fields :as update-potential-fields]
            [clojure.group :as group]
            [clojure.inc-zoom :refer [inc-zoom!]]
            [clojure.k-cursor :refer [k->cursor]]
            [clojure.k-destroy :refer [k->destroy]]
            [clojure.k-handle-input :refer [k->handle-input]]
            [clojure.key-just-pressed :refer [f] :rename {f key-just-pressed?}]
            [clojure.key-pressed :refer [f] :rename {f key-pressed?}]
            [clojure.line-of-sight :as line-of-sight?]
            [clojure.max-delta :refer [max-delta]]
            [clojure.mouse-position :refer [mouse-position]]
            [clojure.mouseover-actor :refer [mouseover-actor]]
            [clojure.actor.mouseover-info :refer [mouseover-actor-info]]
            [clojure.orthographic-camera :as orthographic-camera]
            [clojure.orthographic-camera-position :as get-position]
            [clojure.orthographic-camera-set-position :as camera-set-position]
            [clojure.pausing :refer [pausing?]]
            [clojure.player-effect-ctx :as player-effect-ctx]
            [clojure.point-to-entities :refer [point->entities]]
            [clojure.raycaster-is-blocked :as blocked?]
            [clojure.action-bar.selected-skill :as selected-skill]
            [clojure.set-ctx :as set-ctx]
            [clojure.actor.set-visible]
            [clojure.sort-by-order :as sort-by-order]
            [clojure.stage :as stage]
            [clojure.state-pause-game :refer [state->pause-game?]]
            [clojure.throwable :as throwable]
            [clojure.tick-component :refer [tick-component]]
            [clojure.unproject :as unproject]
            [clojure.usable-state :as usable-state]
            [clojure.validate-humanize :refer [validate-humanize]]
            [clojure.actor.visible]
            [clojure.visible-tiles :refer [visible-tiles]]
            [clojure.world-unit-scale :refer [world-unit-scale]]
            [clojure.gl20 :as gl20]
            [clojure.zoom-speed :refer [zoom-speed]]))

(def ^:private entity-render-layers
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
(defn- render-validate-step [ctx]
  (validate-humanize schema ctx)
  ctx)

(defn- update-mouse-positions-step
  [{:keys [ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (let [mp (mouse-position ctx)]
    (-> ctx
        (assoc :ctx/world-mouse-position (unproject/f world-viewport mp))
        (assoc :ctx/ui-mouse-position (-> stage :stage/viewport (unproject/f mp))))))

(defn- update-mouseover-eid-step
  [{:keys [ctx/mouseover-eid
           ctx/player-eid
           ctx/grid
           ctx/raycaster
           ctx/render-z-order
           ctx/world-mouse-position]
    :as ctx}]
  (let [position world-mouse-position
        new-eid (if (mouseover-actor ctx)
                  nil
                  (let [player @player-eid
                        hits (remove #(= (:body/z-order (:entity/body @%)) :z-order/effect)
                                     (point->entities grid position))]
                    (->> render-z-order
                         (sort-by-order/f hits #(:body/z-order (:entity/body @%)))
                         reverse
                         (filter #(line-of-sight?/f raycaster player @%))
                         first)))]
    (when mouseover-eid
      (swap! mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (assoc ctx :ctx/mouseover-eid new-eid)))

(defn- check-debug-viewer-step
  [{:keys [ctx/controls
           ctx/mouseover-eid
           ctx/skin
           ctx/stage
           ctx/grid
           ctx/world-mouse-position]
    :as ctx}]
  (when (button-just-pressed? ctx (:open-debug-button controls))
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

(defn- set-active-entities-step
  [{:keys [ctx/player-eid
           ctx/content-grid]
    :as ctx}]
  (assoc ctx :ctx/active-entities
         (active-entities/f content-grid @player-eid)))

(defn- set-camera-position-step
  [{:keys [ctx/player-eid
           ctx/world-viewport]
    :as ctx}]
  (camera-set-position/set-position! (:viewport/camera world-viewport)
                                     (:body/position (:entity/body @player-eid)))
  ctx)

(defn- clear-screen-step
  [{:keys [ctx/graphics] :as ctx}]
  (let [gl (graphics/get-gl20 graphics)]
    (gl20/clear-color! gl 0 0 0 0)
    (gl20/clear! gl gl20/color-buffer-bit))
  ctx)

(defn- render-draw-tiled-map-step
  [{:keys [ctx/batch
           ctx/colors
           ctx/explored-tile-corners
           ctx/raycaster
           ctx/tiled-map
           ctx/world-viewport]
    :as ctx}]
  (draw-tiled-map/f! batch
                     world-unit-scale
                     (:viewport/camera world-viewport)
                     tiled-map
                     (tile-color-setter*
                      {:ray-blocked? (partial blocked?/f raycaster)
                       :explored-tile-corners explored-tile-corners
                       :light-position (get-position/f (:viewport/camera world-viewport))
                       :see-all-tiles? false
                       :explored-tile-color (:colors/explored-tile colors)
                       :visible-tile-color (:colors/visible-tile colors)
                       :invisible-tile-color (:colors/invisible-tile colors)}))
  ctx)

(defn- draw-cell-debug
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-viewport
           ctx/show-potential-field-colors?
           ctx/show-cell-entities?
           ctx/show-cell-occupied?]}]
  (apply concat
         (for [[x y] (visible-tiles (:viewport/camera world-viewport))
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

(defn- draw-entities!
  [{:keys [ctx/active-entities
           ctx/colors
           ctx/player-eid
           ctx/raycaster
           ctx/render-z-order
           ctx/show-body-bounds?]
    :as ctx}
   render-layers]
  (let [entities (map deref active-entities)
        player @player-eid
        should-draw? (fn [entity z-order]
                       (or (= z-order :z-order/effect)
                           (line-of-sight?/f raycaster player entity)))]
    (doseq [[z-order entities] (sort-by-order/f (group-by (comp :body/z-order :entity/body) entities)
                                                first
                                                render-z-order)
            render-layer render-layers
            entity entities
            :when (should-draw? entity z-order)]
      (try
        (do
          (when show-body-bounds?
            (draw! ctx (draw-rectangle/f (:entity/body entity)
                                         (if (:body/collides? (:entity/body entity))
                                           (:colors/debug-body-outline-collides colors)
                                           (:colors/debug-body-outline colors)))))
          (doseq [[k v] entity
                  :when (get render-layer k)]
            (draw! ctx (draw-component ctx entity k v))))
        (catch Throwable t
          (draw! ctx (draw-rectangle/f (:entity/body entity) (:colors/debug-body-outline-render-error colors)))
          (throwable/pretty-pst t))))))

(defn- highlight-mouseover-tile
  [{:keys [ctx/colors
           ctx/grid
           ctx/world-mouse-position]}]
  (let [[x y] (mapv int world-mouse-position)
        cell (grid [x y])]
    (when (and cell (#{:air :none} (:movement @cell)))
      [[:draw/rectangle x y 1 1
        (case (:movement @cell)
          :air (:colors/mouseover-tile-air colors)
          :none (:colors/mouseover-tile-none colors))]])))

(defn- draw-on-world-viewport-step
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  (batch/set-color! batch 1 1 1 1)
  (batch/set-projection-matrix! batch (orthographic-camera/combined (:viewport/camera world-viewport)))
  (batch/begin! batch)
  (let [old-line-width (shape-drawer/get-default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [[f & params] draw-fns]
      (draw! ctx (apply f ctx params)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (batch/end! batch)
  ctx)

(defn- assoc-interaction-state-create
  [{:keys [ctx/mouseover-eid
           ctx/stage
           ctx/player-eid
           ctx/world-mouse-position]
    :as ctx}]
  (let [mouseover-actor* (mouseover-actor ctx)]
    (cond
      mouseover-actor*
      [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor*)]

      (and mouseover-eid
           (:entity/clickable @mouseover-eid))
      [:interaction-state/clickable-mouseover-eid
       {:clicked-eid mouseover-eid
        :in-click-range? (< (distance/f (:entity/body @player-eid)
                                        (:entity/body @mouseover-eid))
                            (:entity/click-distance-tiles @player-eid))}]

      :else
      (if-let [skill-id (-> stage
                            :stage/root
                            (#(group/find-actor % "moon.ui.action-bar"))
                            selected-skill/f)]
        (let [entity @player-eid
              skill (skill-id (:entity/skills entity))
              effect-ctx (player-effect-ctx/f mouseover-eid world-mouse-position player-eid)
              state (usable-state/f skill entity effect-ctx)]
          (if (= state :usable)
            [:interaction-state.skill/usable [skill effect-ctx]]
            [:interaction-state.skill/not-usable state]))
        [:interaction-state/no-skill-selected]))))

(defn- set-cursor-step
  [{:keys [ctx/graphics
           ctx/cursors
           ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        f (k->cursor state-k)
        cursor-key (if (keyword? f)
                     f
                     (f eid ctx))]
    (assert (contains? cursors cursor-key))
    (graphics/set-cursor! graphics (get cursors cursor-key)))
  ctx)

(defn- handle-player-input-step
  [{:keys [ctx/player-eid]
    :as ctx}]
  (let [eid player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        txs (if-let [f (k->handle-input state-k)]
              (f eid ctx)
              nil)]
    (do! ctx txs))
  ctx)

(defn- assoc-paused-step
  [{:keys [ctx/input
           ctx/controls
           ctx/player-eid]
    :as ctx}]
  (assoc ctx :ctx/paused?
         (or #_error
             (and pausing?
                  (state->pause-game? (:state (:entity/fsm @player-eid)))
                  (not (or (key-just-pressed? input (:unpause-once controls))
                           (key-pressed? input (:unpause-continously controls))))))))

(defn- update-time-step
  [{:keys [ctx/graphics]
    :as ctx}]
  (let [delta-ms (min (graphics/get-delta-time graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn- if-not-paused-update-potential-fields-step
  [{:keys [ctx/active-entities
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (update-potential-fields/tick! grid
                                   potential-field-cache
                                   faction
                                   active-entities
                                   max-iterations))
  ctx)

(defn- tick-entities-step
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
    (do! ctx
         (mapcat (fn [eid]
                   (mapcat (fn [component]
                             (try (tick-component ctx eid component)
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

(defn- remove-destroyed-entities-step
  [ctx]
  (do! ctx
       (mapcat
        (fn [eid]
          (cons
           [:tx/unregister-eid eid]
           (mapcat (fn [[k v]]
                     (if-let [f (k->destroy k)]
                       (f v eid)
                       nil))
                   @eid)))
        (filter (comp :entity/destroyed? deref)
                (vals @(:ctx/entity-ids ctx)))))
  ctx)

(defn- window-camera-controls-step
  [{:keys [ctx/input
           ctx/controls
           ctx/stage
           ctx/world-viewport]
    :as ctx}]
  (when (key-pressed? input (:zoom-in controls))
    (inc-zoom! (:viewport/camera world-viewport) zoom-speed))

  (when (key-pressed? input (:zoom-out controls))
    (inc-zoom! (:viewport/camera world-viewport) (- zoom-speed)))

  (when (key-just-pressed? input (:close-windows-key controls))
    (->> (group/find-actor (:stage/root stage) "moon.ui.windows")
         group/get-children
         (run! #(clojure.actor.set-visible/f % false))))

  (when (key-just-pressed? input (:toggle-inventory controls))
    (let [inventory (group/find-actor (:stage/root stage) "moon.ui.windows.inventory")]
      (clojure.actor.set-visible/f inventory (not (clojure.actor.visible/f inventory)))))

  (when (key-just-pressed? input (:toggle-entity-info controls))
    (let [entity-info (group/find-actor (:stage/root stage) "moon.ui.windows.entity-info")]
      (clojure.actor.set-visible/f entity-info (not (clojure.actor.visible/f entity-info)))))
  ctx)

(defn- update-draw-stage-step
  [{:keys [ctx/stage] :as ctx}]
  (set-ctx/f stage ctx)
  (stage/act! stage)
  (stage/draw! stage)
  (:stage/ctx stage))

(defn render
  [{:keys [ctx/stage]
    :as ctx}]
  (let [ctx (or (:stage/ctx stage) ctx)
        ctx (render-validate-step ctx)
        ctx (update-mouse-positions-step ctx)
        ctx (update-mouseover-eid-step ctx)
        ctx (check-debug-viewer-step ctx)
        ctx (set-active-entities-step ctx)
        ctx (set-camera-position-step ctx)
        ctx (clear-screen-step ctx)
        ctx (render-draw-tiled-map-step ctx)
        ctx (draw-on-world-viewport-step ctx
                                         [[draw-cell-debug]
                                          [draw-entities! entity-render-layers]
                                          [highlight-mouseover-tile]])
        ctx (assoc ctx :ctx/interaction-state (assoc-interaction-state-create ctx))
        ctx (set-cursor-step ctx)
        ctx (handle-player-input-step ctx)
        ctx (dissoc ctx :ctx/interaction-state)
        ctx (assoc-paused-step ctx)
        ctx (if (:ctx/paused? ctx)
              ctx
              (reduce (fn [ctx f] (f ctx))
                      ctx
                      [update-time-step
                       if-not-paused-update-potential-fields-step
                       tick-entities-step]))
        ctx (remove-destroyed-entities-step ctx)
        ctx (window-camera-controls-step ctx)
        ctx (update-draw-stage-step ctx)]
    (render-validate-step ctx)))
