(ns game.listener.render
  (:require [game.shared :refer [active-entities
                                 assoc-interaction-state
                                 audiovisual!
                                 colors
                                 controls
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
                                 tick-component
                                 tiled-map
                                 tile-color-setter*
                                 toggle-inventory-visible!
                                 ui-mouse-position
                                 unit-scale
                                 update-potential-fields
                                 update-time
                                 world
                                 world-mouse-position]]
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

(defn update-mouseover-eid! [stage world-viewport]
  (let [old-mouseover-eid @mouseover-eid
        [x y] (ui-mouse-position stage)
        new-eid (if (mouseover-actor stage x y)
                  nil
                  (let [player @@player-eid
                        hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                     (world/point->entities @world (world-mouse-position world-viewport)))]
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

(defn set-camera-to-player! [world-viewport]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        pos (.position camera)
        [x y] (:entity/position @@player-eid)]
    (set! (.x pos) x)
    (set! (.y pos) y)
    (.update camera)))

(defn draw-tiled-map! [batch world-viewport world-unit-scale]
  (let [raycaster @raycaster
        tiled-map @tiled-map
        ^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        pos (.position camera)]
    (moon-tiled-map/draw! tiled-map
                          batch
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

(defn current-mouseover-actor [stage]
  (let [[x y] (ui-mouse-position stage)]
    (mouseover-actor stage x y)))

(defn draw-world! [batch default-font shape-drawer stage textures world-viewport world-unit-scale]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        mouseover-actor* (current-mouseover-actor stage)
        world-mouse-pos (world-mouse-position world-viewport)]
    (.setColor ^Batch batch (float 1) (float 1) (float 1) (float 1))
    (.setProjectionMatrix ^Batch batch (.combined camera))
    (.begin ^Batch batch)
    (let [old-line-width (.getDefaultLineWidth ^ShapeDrawer shape-drawer)]
      (.setDefaultLineWidth ^ShapeDrawer shape-drawer (* world-unit-scale old-line-width))
      (reset! unit-scale world-unit-scale)
      (doseq [draw-fn [#(draw-tile-grid % shape-drawer world-viewport)
                       #(draw-cell-debug % shape-drawer world-viewport)
                       #(draw-entities! % shape-drawer batch default-font textures unit-scale world-unit-scale mouseover-actor* world-mouse-pos)
                       #(highlight-mouseover-tile % shape-drawer world-mouse-pos)]]
        (draw-fn nil))
      (reset! unit-scale 1)
      (.setDefaultLineWidth ^ShapeDrawer shape-drawer old-line-width))
    (.end ^Batch batch)))

(defn update-cursor! [cursors]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-fn (k->cursor state-k)
        cursor-key (if (keyword? cursor-fn)
                     cursor-fn
                     (cursor-fn eid))]
    (assert (contains? cursors cursor-key))
    (.setCursor ^Graphics Gdx/graphics ^Cursor (get cursors cursor-key))))

(defn- movement-vector [key-pressed?]
  (let [r (when (key-pressed? Input$Keys/D) [1  0])
        l (when (key-pressed? Input$Keys/A) [-1 0])
        u (when (key-pressed? Input$Keys/W) [0  1])
        d (when (key-pressed? Input$Keys/S) [0 -1])]
    (when (or r l u d)
      (let [v (v2/normalise (reduce v2/add [0 0] (remove nil? [r l u d])))]
        (when (pos? (v2/length v))
          v)))))

(defn handle-player-input! [audio skin stage textures world-viewport key-pressed? button-just-pressed?]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))]
    (handle-input state-k eid nil audio skin stage textures
                  (button-just-pressed? Input$Buttons/LEFT)
                  (movement-vector key-pressed?)
                  (current-mouseover-actor stage)
                  (world-mouse-position world-viewport))))

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

(defn- tick-entities! [audio skin stage textures world-viewport]
  (let [active-entities @active-entities
        raycaster @raycaster
        elapsed-time @elapsed-time
        world-mouse-pos (world-mouse-position world-viewport)]
    (letfn [(apply-effects! [effect-ctx effects]
              (doseq [effect (filter #(effect-applicable? % effect-ctx) effects)]
                (handle-effect audio skin stage textures
                               effect effect-ctx world-mouse-pos
                               apply-effects!
                               active-entities
                               colors
                               raycaster
                               elapsed-time)))]
      (doseq [eid active-entities
              component @eid]
        (tick-component audio skin stage textures
                        nil world-mouse-pos
                        apply-effects!
                        eid component)))))

(defn tick-game! [audio skin stage textures world-viewport]
  (when-not @paused?
    (update-time)
    (update-potential-fields)
    (tick-entities! audio skin stage textures world-viewport)))

(defn destroy-entities! [audio skin stage textures]
  (doseq [eid (world/destroyed-eids @world)]
    (world/unregister-eid! @world eid)
    (doseq [[k v] @eid]
      (case k
        :entity/destroy-audiovisual
        (audiovisual! audio skin stage textures (:entity/position @eid) v)
        nil))))

(defn- zoom-in! [world-viewport]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)]
    (set! (.zoom camera) (max 0.1 (+ (.zoom camera) 0.025)))
    (.update camera)))

(defn- zoom-out! [world-viewport]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)]
    (set! (.zoom camera) (max 0.1 (+ (.zoom camera) -0.025)))
    (.update camera)))

(defn- close-windows! [stage]
  (->> (.getChildren ^Group (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows"))
       (run! (fn [actor]
               (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor actor false)))))

(defn- toggle-inventory! [stage]
  (toggle-inventory-visible! stage))

(defn- toggle-entity-info! [stage]
  (let [entity-info (.findActor ^Group (.getRoot ^Stage stage) "moon.ui.windows.entity-info")]
    (.setVisible ^com.badlogic.gdx.scenes.scene2d.Actor entity-info
                 (not (.isVisible ^com.badlogic.gdx.scenes.scene2d.Actor entity-info)))))

(defn handle-controls! [stage world-viewport key-pressed? key-just-pressed?]
  (doseq [[k f] {(:zoom-in controls) #(zoom-in! world-viewport)
                 (:zoom-out controls) #(zoom-out! world-viewport)}]
    (when (key-pressed? k)
      (f)))
  (doseq [[k f] {(:close-windows-key controls) #(close-windows! stage)
                 (:toggle-inventory controls) #(toggle-inventory! stage)
                 (:toggle-entity-info controls) #(toggle-entity-info! stage)}]
    (when (key-just-pressed? k)
      (f))))
