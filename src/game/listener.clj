(ns game.listener
  (:require [game.listener.create :refer [bind-player-eid!
                                          create-audio!
                                          create-batch!
                                          create-cursors!
                                          create-default-font!
                                          create-explored-tile-corners!
                                          create-level!
                                          create-raycaster!
                                          create-shape-drawer!
                                          create-shape-drawer-texture!
                                          create-skin!
                                          create-stage!
                                          create-textures!
                                          create-ui-actors
                                          create-world!
                                          create-world-viewport!
                                          init-tooltip-manager!
                                          put-pretty-name-color!
                                          spawn-map-creatures!
                                          spawn-player!]]
            [game.listener.render :refer [clear-interaction-state!
                                          current-mouseover-actor
                                          destroy-entities!
                                          draw-tiled-map!
                                          handle-controls!
                                          handle-player-input!
                                          set-camera-to-player!
                                          tick-game!
                                          update-active-entities!
                                          update-cursor!
                                          update-mouseover-eid!
                                          update-paused!]]
            [game.listener.render.draw-world :refer [draw-world!]]
            [game.shared :refer [action-bar-selected-skill
                                 mouseover-actor-info
                                 player-effect-ctx
                                 skill-usable-state
                                 world-mouse-position]]
            [moon.db :as db]
            [moon.v2 :as v2])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input InputProcessor)
           (com.badlogic.gdx.scenes.scene2d Actor Stage)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def world-unit-scale (float (/ 48)))

(def audio (atom nil))
(def batch (atom nil))
(def cursors (atom nil))
(def default-font (atom nil))
(def world-viewport (atom nil))
(def shape-drawer (atom nil))
(def shape-drawer-texture (atom nil))
(def textures (atom nil))
(def skin (atom nil))
(def stage (atom nil))

(def db (db/create))
(def world (atom nil))
(def tiled-map (atom nil))
(def start-position (atom nil))
(def raycaster (atom nil))
(def player-eid (atom nil))
(def explored-tile-corners (atom nil))
(def potential-field-cache (atom nil))
(def active-entities (atom nil))
(def delta-time (atom nil))
(def mouseover-eid (atom nil))
(def elapsed-time (atom 0))
(def paused? (atom false))
(def show-potential-field-colors? (atom nil))
(def show-cell-entities? (atom false))
(def show-cell-occupied? (atom false))
(def show-body-bounds? (atom false))
(def show-tile-grid? (atom false))
(def interaction-state (atom nil))
(def unit-scale (atom 1))

(def factions-iterations
  {:good 15
   :evil 5})

(def listener
  (reify ApplicationListener
    (create [_]
      (reset! audio (create-audio! Gdx/audio Gdx/files))
      (reset! batch (create-batch!))
      (reset! unit-scale 1)
      (reset! shape-drawer-texture (create-shape-drawer-texture!))
      (reset! shape-drawer (create-shape-drawer! @batch @shape-drawer-texture))
      (reset! skin (create-skin! Gdx/files))
      (let [s (create-stage! @batch)]
        (.setInputProcessor ^Input Gdx/input ^InputProcessor s)
        (reset! stage s))
      (init-tooltip-manager!)
      (put-pretty-name-color!)
      (reset! cursors (create-cursors! Gdx/files))
      (reset! textures (create-textures! Gdx/files))
      (reset! world-viewport (create-world-viewport! world-unit-scale))
      (reset! default-font (create-default-font! Gdx/files))
      (doseq [^Actor actor (create-ui-actors world
                                             elapsed-time
                                             mouseover-eid
                                             paused?
                                             player-eid
                                             show-tile-grid?
                                             show-cell-entities?
                                             show-cell-occupied?
                                             show-body-bounds?
                                             show-potential-field-colors?
                                             unit-scale
                                             @audio
                                             @default-font
                                             @shape-drawer
                                             @skin
                                             @stage
                                             @textures
                                             @world-viewport)]
        (.addActor ^Stage @stage actor))
      (let [{level-tiled-map :tiled-map
             level-start :start-position} (create-level! db @textures)]
        (reset! tiled-map level-tiled-map)
        (reset! start-position level-start))
      (reset! world (create-world! @tiled-map))
      (reset! explored-tile-corners (create-explored-tile-corners! @tiled-map))
      (reset! raycaster (create-raycaster! @world))
      (spawn-player! db world elapsed-time start-position @skin @stage @textures)
      (bind-player-eid! world player-eid)
      (spawn-map-creatures! db world elapsed-time start-position tiled-map @skin @stage @textures))

    (dispose [_]
      (run! Disposable/.dispose (vals @audio))
      (Disposable/.dispose @batch)
      (run! Disposable/.dispose (vals @cursors))
      (Disposable/.dispose @default-font)
      (Disposable/.dispose @shape-drawer-texture)
      (Disposable/.dispose @skin)
      (run! Disposable/.dispose (vals @textures))
      (Disposable/.dispose @tiled-map))

    (render [_]
      (let [input Gdx/input
            key-pressed? #(.isKeyPressed ^Input input (int %))
            key-just-pressed? #(.isKeyJustPressed ^Input input (int %))
            button-just-pressed? #(.isButtonJustPressed ^Input input (int %))
            audio @audio
            batch @batch
            cursors @cursors
            default-font @default-font
            shape-drawer @shape-drawer
            skin @skin
            stage @stage
            textures @textures
            world-viewport @world-viewport]
        (ScreenUtils/clear 0 0 0 0)
        (update-mouseover-eid! world raycaster player-eid mouseover-eid stage world-viewport)
        (update-active-entities! world player-eid active-entities)
        (set-camera-to-player! player-eid world-viewport)
        (draw-tiled-map! batch world-viewport tiled-map raycaster explored-tile-corners world-unit-scale)
        (draw-world! batch default-font shape-drawer stage textures world-viewport world-unit-scale unit-scale
                     world player-eid raycaster elapsed-time show-body-bounds? active-entities
                     show-tile-grid? show-cell-entities? show-cell-occupied? show-potential-field-colors?
                     factions-iterations)
        (reset! interaction-state
                (let [player-eid @player-eid
                      mouseover-eid @mouseover-eid
                      mouseover-actor (current-mouseover-actor stage)
                      world-mouse-position (world-mouse-position world-viewport)]
                  (cond
                    mouseover-actor
                    [:interaction-state/mouseover-actor (mouseover-actor-info mouseover-actor)]

                    (and mouseover-eid
                         (:entity/clickable @mouseover-eid))
                    [:interaction-state/clickable-mouseover-eid
                     {:clicked-eid mouseover-eid
                      :in-click-range? (< (v2/distance (:entity/position @player-eid)
                                                       (:entity/position @mouseover-eid))
                                          (:entity/click-distance-tiles @player-eid))}]

                    :else
                    (if-let [skill-id (-> (.getRoot ^Stage stage)
                                          (.findActor "moon.ui.action-bar")
                                          action-bar-selected-skill)]
                      (let [entity @player-eid
                            skill (skill-id (:entity/skills entity))
                            effect-ctx (player-effect-ctx mouseover-eid world-mouse-position player-eid)
                            state (skill-usable-state skill entity effect-ctx)]
                        (if (= state :usable)
                          [:interaction-state.skill/usable [skill effect-ctx]]
                          [:interaction-state.skill/not-usable state]))
                      [:interaction-state/no-skill-selected]))))
        (update-cursor! cursors interaction-state player-eid)
        (handle-player-input! world elapsed-time interaction-state player-eid
                              audio skin stage textures world-viewport
                              key-pressed? button-just-pressed?)
        (clear-interaction-state! interaction-state)
        (update-paused! paused? player-eid key-pressed? key-just-pressed?)
        (tick-game! db world raycaster elapsed-time delta-time potential-field-cache active-entities paused?
                    audio skin stage textures world-viewport factions-iterations)
        (destroy-entities! db world elapsed-time audio skin stage textures)
        (handle-controls! stage world-viewport key-pressed? key-just-pressed?)
        (let [^Stage stage stage]
          (.act stage)
          (.draw stage))))

    (resize [_ width height]
      (.update (.getViewport ^Stage @stage) width height true)
      (.update ^Viewport @world-viewport width height false))

    (pause [_])

    (resume [_])))
