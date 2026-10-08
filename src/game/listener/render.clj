(ns game.listener.render
  (:require [game.audio :refer [audiovisual!]]
            [game.colors :refer [colors]]
            [game.controls :refer [controls]]
            [game.mouse :refer [mouseover-actor ui-mouse-position world-mouse-position]]
            [game.tile-color :refer [tile-color-setter*]]
            [moon.coll :as coll]
            [moon.content-grid :as content-grid]
            [moon.grid :as grid]
            [moon.raycaster :as raycaster]
            [moon.tiled-map :as moon-tiled-map]
            [world.unregister-eid :refer [unregister-eid!]])
  (:import (com.badlogic.gdx Gdx Graphics)
           (com.badlogic.gdx.graphics Cursor OrthographicCamera)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn update-mouseover-eid! [world raycaster player-eid mouseover-eid stage world-viewport render-z-order]
  (let [old-mouseover-eid @mouseover-eid
        [x y] (ui-mouse-position stage)
        new-eid (if (mouseover-actor stage x y)
                  nil
                  (let [player @@player-eid
                        hits (remove #(= (:entity/z-order @%) :z-order/effect)
                                     (grid/point->entities (:world/grid @world) (world-mouse-position world-viewport)))]
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
  (reset! active-entities (content-grid/active-entities (:world/content-grid @world) @@player-eid)))

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

(defn destroy-entities! [db world elapsed-time audio skin stage textures z-orders minimum-size]
  (doseq [eid (filter (comp :entity/destroyed? deref) (vals @(:world/entity-ids @world)))]
    (unregister-eid! @world eid)
    (doseq [[k v] @eid]
      (case k
        :entity/destroy-audiovisual
        (audiovisual! db world elapsed-time audio skin stage textures z-orders minimum-size (:entity/position @eid) v)
        nil))))
