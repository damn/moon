(ns game.listener
  (:require [audio.create :refer [create-audio!]]
            [audio.dispose :as audio.dispose]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [files.create :refer [create-sound-file-handles!]]
            [game.listener.create :refer [bind-player-eid!
                                          create-cursors!
                                          create-default-font!
                                          create-explored-tile-corners!
                                          create-level!
                                          create-raycaster!
                                          create-shape-drawer!
                                          create-skin!
                                          create-stage!
                                          create-textures!
                                          create-world!
                                          create-world-viewport!
                                          init-tooltip-manager!
                                          put-pretty-name-color!
                                          spawn-map-creatures!
                                          spawn-player!]]
            [texture.white-pixel :refer [white-pixel-texture]]
            [game.listener.create.ui-actors :refer [create-ui-actors]]
            [game.listener.render :refer [clear-interaction-state!
                                          current-mouseover-actor
                                          destroy-entities!
                                          draw-tiled-map!
                                          handle-controls!
                                          set-camera-to-player!
                                          update-active-entities!
                                          update-cursor!
                                          update-mouseover-eid!
                                          update-paused!]]
            [game.listener.render.draw-world :refer [draw-world!]]
            [game.listener.render.handle-player-input :refer [handle-player-input!]]
            [game.listener.render.tick-world :refer [tick-game!]]
            [game.mouse :refer [mouseover-actor-info world-mouse-position]]
            [game.ui :refer [action-bar-selected-skill]]
            [moon.db :as db]
            [moon.v2 :as v2]
            [skill.usable-state :refer [usable-state]])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input InputProcessor)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d Actor Stage)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def listener
  (let [world-unit-scale (float (/ 48))
        minimum-size 0.39
        max-delta 0.04
        max-speed (/ minimum-size max-delta)
        z-orders [:z-order/on-ground
                  :z-order/ground
                  :z-order/flying
                  :z-order/effect]
        render-z-order (apply hash-map (interleave z-orders (range)))
        factions-iterations {:good 15
                             :evil 5}
        sound-paths (-> "sounds.edn" io/resource slurp edn/read-string)
        audio (atom nil)
        batch (atom nil)
        cursors (atom nil)
        default-font (atom nil)
        world-viewport (atom nil)
        shape-drawer (atom nil)
        shape-drawer-texture (atom nil)
        textures (atom nil)
        skin (atom nil)
        stage (atom nil)
        db (db/create)
        world (atom nil)
        tiled-map (atom nil)
        start-position (atom nil)
        raycaster (atom nil)
        player-eid (atom nil)
        explored-tile-corners (atom nil)
        potential-field-cache (atom nil)
        active-entities (atom nil)
        delta-time (atom nil)
        mouseover-eid (atom nil)
        elapsed-time (atom 0)
        paused? (atom false)
        show-potential-field-colors? (atom nil)
        show-cell-entities? (atom false)
        show-cell-occupied? (atom false)
        show-body-bounds? (atom false)
        show-tile-grid? (atom false)
        interaction-state (atom nil)
        unit-scale (atom 1)]
    (reify ApplicationListener
      (create [_]
        (reset! audio (create-audio! Gdx/audio
                                     (create-sound-file-handles! Gdx/files sound-paths)))
        (reset! batch (SpriteBatch.))
        (reset! unit-scale 1)
        (reset! shape-drawer-texture (white-pixel-texture))
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
                                               z-orders
                                               minimum-size
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
        (spawn-player! db world elapsed-time start-position @skin @stage @textures z-orders minimum-size)
        (bind-player-eid! world player-eid)
        (spawn-map-creatures! db world elapsed-time start-position tiled-map z-orders minimum-size))

      (dispose [_]
        (audio.dispose/dispose! @audio)
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
          (update-mouseover-eid! world raycaster player-eid mouseover-eid stage world-viewport render-z-order)
          (update-active-entities! world player-eid active-entities)
          (set-camera-to-player! player-eid world-viewport)
          (draw-tiled-map! batch world-viewport tiled-map raycaster explored-tile-corners world-unit-scale)
          (draw-world! batch default-font shape-drawer stage textures world-viewport world-unit-scale unit-scale
                       world player-eid raycaster elapsed-time show-body-bounds? active-entities
                       show-tile-grid? show-cell-entities? show-cell-occupied? show-potential-field-colors?
                       factions-iterations render-z-order)
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
                              target-position (or (and mouseover-eid
                                                       (:entity/position @mouseover-eid))
                                                  world-mouse-position)
                              effect-ctx {:effect/source player-eid
                                          :effect/target mouseover-eid
                                          :effect/target-position target-position
                                          :effect/target-direction (v2/direction (:entity/position @player-eid)
                                                                                target-position)}
                              state (usable-state skill entity effect-ctx)]
                          (if (= state :usable)
                            [:interaction-state.skill/usable [skill effect-ctx]]
                            [:interaction-state.skill/not-usable state]))
                        [:interaction-state/no-skill-selected]))))
          (update-cursor! cursors interaction-state player-eid)
          (handle-player-input! world elapsed-time interaction-state player-eid
                                audio skin stage textures z-orders minimum-size world-viewport
                                key-pressed? button-just-pressed?)
          (clear-interaction-state! interaction-state)
          (update-paused! paused? player-eid key-pressed? key-just-pressed?)
          (tick-game! db world raycaster elapsed-time delta-time potential-field-cache active-entities paused?
                      audio skin stage textures world-viewport factions-iterations z-orders
                      minimum-size max-delta max-speed)
          (destroy-entities! db world elapsed-time audio skin stage textures z-orders minimum-size)
          (handle-controls! stage world-viewport key-pressed? key-just-pressed?)
          (let [^Stage stage stage]
            (.act stage)
            (.draw stage))))

      (resize [_ width height]
        (.update (.getViewport ^Stage @stage) width height true)
        (.update ^Viewport @world-viewport width height false))

      (pause [_])

      (resume [_]))))
