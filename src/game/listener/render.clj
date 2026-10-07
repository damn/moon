(ns game.listener.render
  (:require [game.shared :refer [active-entities
                                 assoc-interaction-state
                                 audio
                                 audiovisual!
                                 batch
                                 colors
                                 controls
                                 cursors
                                 default-font
                                 draw-cell-debug
                                 draw-entities!
                                 draw-tile-grid
                                 effect-applicable?
                                 elapsed-time
                                 explored-tile-corners
                                 handle-effect
                                 handle-input
                                 highlight-mouseover-tile
                                 interaction-state
                                 k->cursor
                                 mouseover-actor
                                 mouseover-eid
                                 paused?
                                 player-eid
                                 raycaster
                                 render-z-order
                                 shape-drawer
                                 stage
                                 tick-component
                                 tiled-map
                                 tile-color-setter*
                                 toggle-inventory-visible!
                                 ui-mouse-position
                                 unit-scale
                                 update-potential-fields
                                 update-time
                                 world
                                 world-mouse-position
                                 world-unit-scale
                                 world-viewport]]
            [moon.coll :as coll]
            [moon.raycaster :as raycaster]
            [moon.tiled-map :as moon-tiled-map]
            [moon.v2 :as v2]
            [moon.world :as world])
  (:import (com.badlogic.gdx Gdx Graphics Input Input$Buttons Input$Keys)
           (com.badlogic.gdx.graphics Cursor OrthographicCamera)
           (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.scenes.scene2d Group Stage)
           (com.badlogic.gdx.utils.viewport Viewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn update-mouseover-eid! []
  (let [old-mouseover-eid @mouseover-eid
        [x y] (ui-mouse-position)
        new-eid (if (mouseover-actor @stage x y)
                  nil
                  (let [player @@player-eid
                        hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                     (world/point->entities @world (world-mouse-position)))]
                    (->> render-z-order
                         (coll/sort-by-order hits #(:entity/z-order @%))
                         reverse
                         (filter #(raycaster/line-of-sight? @raycaster player @%))
                         first)))]
    (when old-mouseover-eid
      (swap! old-mouseover-eid dissoc :entity/mouseover?))
    (when new-eid
      (swap! new-eid assoc :entity/mouseover? true))
    (reset! mouseover-eid new-eid)))

(defn update-active-entities! []
  (reset! active-entities (world/active-entities @world @@player-eid)))

(defn set-camera-to-player! []
  (let [^OrthographicCamera camera (.getCamera ^Viewport @world-viewport)
        pos (.position camera)
        [x y] (:entity/position @@player-eid)]
    (set! (.x pos) x)
    (set! (.y pos) y)
    (.update camera)))

(defn draw-tiled-map! []
  (let [raycaster @raycaster
        ^Viewport world-viewport @world-viewport
        tiled-map @tiled-map
        ^OrthographicCamera camera (.getCamera world-viewport)
        pos (.position camera)]
    (moon-tiled-map/draw! tiled-map
                          @batch
                          world-unit-scale
                          camera
                          (tile-color-setter*
                           {:ray-blocked? (partial raycaster/blocked? raycaster)
                            :explored-tile-corners explored-tile-corners
                            :light-position [(.x pos) (.y pos) (.z pos)]
                            :see-all-tiles? false
                            :explored-tile-color (:colors/explored-tile colors)
                            :visible-tile-color (:colors/visible-tile colors)
                            :invisible-tile-color (:colors/invisible-tile colors)}))))

(defn current-mouseover-actor []
  (let [[x y] (ui-mouse-position)]
    (mouseover-actor @stage x y)))

(defn draw-world! []
  (let [default-font @default-font
        shape-drawer @shape-drawer
        ^Viewport world-viewport @world-viewport
        ^OrthographicCamera camera (.getCamera world-viewport)
        mouseover-actor* (current-mouseover-actor)]
    (.setColor ^Batch @batch (float 1) (float 1) (float 1) (float 1))
    (.setProjectionMatrix ^Batch @batch (.combined camera))
    (.begin ^Batch @batch)
    (let [old-line-width (.getDefaultLineWidth ^ShapeDrawer shape-drawer)]
      (.setDefaultLineWidth ^ShapeDrawer shape-drawer (* world-unit-scale old-line-width))
      (reset! unit-scale world-unit-scale)
      (doseq [draw-fn [#(draw-tile-grid % shape-drawer world-viewport)
                       #(draw-cell-debug % shape-drawer world-viewport)
                       #(draw-entities! % shape-drawer @batch default-font unit-scale mouseover-actor* (world-mouse-position))
                       #(highlight-mouseover-tile % shape-drawer (world-mouse-position))]]
        (draw-fn nil))
      (reset! unit-scale 1)
      (.setDefaultLineWidth ^ShapeDrawer shape-drawer old-line-width))
    (.end ^Batch @batch)))

(defn update-cursor! []
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-fn (k->cursor state-k)
        cursor-key (if (keyword? cursor-fn)
                     cursor-fn
                     (cursor-fn eid))]
    (assert (contains? @cursors cursor-key))
    (.setCursor ^Graphics Gdx/graphics ^Cursor (get @cursors cursor-key))))

(defn- movement-vector [key-pressed?]
  (let [r (when (key-pressed? Input$Keys/D) [1  0])
        l (when (key-pressed? Input$Keys/A) [-1 0])
        u (when (key-pressed? Input$Keys/W) [0  1])
        d (when (key-pressed? Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v2/normalise (reduce v2/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2/length v))
          v)))))

(defn handle-player-input! [key-pressed? button-just-pressed?]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))]
    (handle-input state-k eid nil @audio
                  (button-just-pressed? Input$Buttons/LEFT)
                  (movement-vector key-pressed?)
                  (current-mouseover-actor)
                  (world-mouse-position))))

(defn clear-interaction-state! []
  (reset! interaction-state nil))

(def pausing? true)

(def state->pause-game?
  {:active-skill false
   :stunned false
   :player-moving false
   :player-idle true
   :player-dead true
   :player-item-on-cursor true})

(defn update-paused! [key-pressed? key-just-pressed?]
  (reset! paused?
          (or #_error
              (and pausing?
                   (state->pause-game? (:state (:entity/fsm @@player-eid)))
                   (not (or (key-just-pressed? (:unpause-once controls))
                            (key-pressed? (:unpause-continously controls))))))))

(defn- tick-entities! []
  (let [active-entities @active-entities
        raycaster @raycaster
        elapsed-time @elapsed-time]
    (letfn [(apply-effects! [effect-ctx effects]
              (doseq [effect (filter #(effect-applicable? % effect-ctx) effects)]
                (handle-effect effect effect-ctx (world-mouse-position)
                               apply-effects!
                               active-entities
                               colors
                               raycaster
                               elapsed-time)))]
      (doseq [eid active-entities
              component @eid]
        (tick-component nil (world-mouse-position)
                        apply-effects!
                        eid component)))))

(defn tick-game! []
  (when-not @paused?
    (update-time)
    (update-potential-fields)
    (tick-entities!)))

(defn destroy-entities! []
  (doseq [eid (world/destroyed-eids @world)]
    (world/unregister-eid! @world eid)
    (doseq [[k v] @eid]
      (case k
        :entity/destroy-audiovisual
        (audiovisual! (:entity/position @eid) v)
        nil))))

(defn- zoom-in! []
  (let [^OrthographicCamera camera (.getCamera ^Viewport @world-viewport)]
    (set! (.zoom camera) (max 0.1 (+ (.zoom camera) 0.025)))
    (.update camera)))

(defn- zoom-out! []
  (let [^OrthographicCamera camera (.getCamera ^Viewport @world-viewport)]
    (set! (.zoom camera) (max 0.1 (+ (.zoom camera) -0.025)))
    (.update camera)))

(defn- close-windows! []
  (->> (.getChildren ^Group (.findActor ^Group (.getRoot ^Stage @stage) "moon.ui.windows"))
       (run! (fn [actor]
               (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor actor false)))))

(defn- toggle-inventory! []
  (toggle-inventory-visible! @stage))

(defn- toggle-entity-info! []
  (let [entity-info (.findActor ^Group (.getRoot ^Stage @stage) "moon.ui.windows.entity-info")]
    (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor entity-info
                 (not (.isVisible ^com.badlogic.gdx.scenes.scene2d.Actor entity-info)))))

(defn handle-controls! [key-pressed? key-just-pressed?]
  (doseq [[k f] {(:zoom-in controls) zoom-in!
                 (:zoom-out controls) zoom-out!}]
    (when (key-pressed? k)
      (f)))
  (doseq [[k f] {(:close-windows-key controls) close-windows!
                 (:toggle-inventory controls) toggle-inventory!
                 (:toggle-entity-info controls) toggle-entity-info!}]
    (when (key-just-pressed? k)
      (f))))
