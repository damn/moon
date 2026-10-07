(ns game.listener.render
  (:require [game.shared :refer [audiovisual!
                                 colors
                                 controls
                                 handle-input
                                 mouseover-actor
                                 tile-color-setter*
                                 toggle-inventory-visible!
                                 ui-mouse-position
                                 world-mouse-position]]
            [moon.coll :as coll]
            [moon.raycaster :as raycaster]
            [moon.tiled-map :as moon-tiled-map]
            [moon.v2 :as v2]
            [moon.world :as world])
  (:import (com.badlogic.gdx Gdx Graphics Input Input$Buttons Input$Keys)
           (com.badlogic.gdx.graphics Cursor OrthographicCamera)
           (com.badlogic.gdx.scenes.scene2d Group Stage)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn update-mouseover-eid! [world raycaster player-eid mouseover-eid stage world-viewport render-z-order]
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

(defn update-active-entities! [world player-eid active-entities]
  (reset! active-entities (world/active-entities @world @@player-eid)))

(defn set-camera-to-player! [player-eid world-viewport]
  (let [^OrthographicCamera camera (.getCamera ^Viewport world-viewport)
        pos (.position camera)
        [x y] (:entity/position @@player-eid)]
    (set! (.x pos) x)
    (set! (.y pos) y)
    (.update camera)))

(defn draw-tiled-map! [batch world-viewport tiled-map raycaster explored-tile-corners world-unit-scale]
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

(defn update-cursor! [cursors interaction-state player-eid]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))
        cursor-fn (get {:player-item-on-cursor :cursors/hand-grab
                        :player-dead :cursors/black-x
                        :active-skill :cursors/sandclock
                        :stunned :cursors/denied
                        :player-moving :cursors/walking
                        :player-idle (fn
                                       [eid]
                                       (let [[k params] @interaction-state]
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
                                           :cursors/no-skill-selected)))}
                       state-k)
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

(defn handle-player-input!
  [world elapsed-time interaction-state player-eid
   audio skin stage textures z-orders world-viewport key-pressed? button-just-pressed?]
  (let [eid @player-eid
        entity @eid
        state-k (:state (:entity/fsm entity))]
    (handle-input world elapsed-time interaction-state
                  state-k eid nil audio skin stage textures z-orders
                  (button-just-pressed? Input$Buttons/LEFT)
                  (movement-vector key-pressed?)
                  (current-mouseover-actor stage)
                  (world-mouse-position world-viewport))))

(defn clear-interaction-state! [interaction-state]
  (reset! interaction-state nil))

(def pausing? true)

(def state->pause-game?
  {:active-skill false
   :stunned false
   :player-moving false
   :player-idle true
   :player-dead true
   :player-item-on-cursor true})

(defn update-paused! [paused? player-eid key-pressed? key-just-pressed?]
  (reset! paused?
          (or #_error
              (and pausing?
                   (state->pause-game? (:state (:entity/fsm @@player-eid)))
                   (not (or (key-just-pressed? (:unpause-once controls))
                            (key-pressed? (:unpause-continously controls))))))))

(defn destroy-entities! [db world elapsed-time audio skin stage textures z-orders]
  (doseq [eid (world/destroyed-eids @world)]
    (world/unregister-eid! @world eid)
    (doseq [[k v] @eid]
      (case k
        :entity/destroy-audiovisual
        (audiovisual! db world elapsed-time audio skin stage textures z-orders (:entity/position @eid) v)
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
