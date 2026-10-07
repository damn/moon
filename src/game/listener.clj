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
                                          draw-world!
                                          handle-controls!
                                          handle-player-input!
                                          set-camera-to-player!
                                          tick-game!
                                          update-active-entities!
                                          update-cursor!
                                          update-mouseover-eid!
                                          update-paused!]]
            [game.shared :refer [assoc-interaction-state
                                 explored-tile-corners
                                 raycaster
                                 start-position
                                 tiled-map
                                 unit-scale
                                 world
                                 world-mouse-position]])
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
      (doseq [^Actor actor (create-ui-actors @audio
                                             @default-font
                                             @shape-drawer
                                             @skin
                                             @stage
                                             @textures
                                             @world-viewport)]
        (.addActor ^Stage @stage actor))
      (let [{level-tiled-map :tiled-map
             level-start :start-position} (create-level! @textures)]
        (reset! tiled-map level-tiled-map)
        (reset! start-position level-start))
      (reset! world (create-world! @tiled-map))
      (reset! explored-tile-corners (create-explored-tile-corners! @tiled-map))
      (reset! raycaster (create-raycaster! @world))
      (spawn-player! @skin @stage @textures)
      (bind-player-eid!)
      (spawn-map-creatures! @skin @stage @textures))

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
        (update-mouseover-eid! stage world-viewport)
        (update-active-entities!)
        (set-camera-to-player! world-viewport)
        (draw-tiled-map! batch world-viewport world-unit-scale)
        (draw-world! batch default-font shape-drawer stage textures world-viewport world-unit-scale)
        (assoc-interaction-state (current-mouseover-actor stage)
                                 (world-mouse-position world-viewport)
                                 stage)
        (update-cursor! cursors)
        (handle-player-input! audio skin stage textures world-viewport
                              key-pressed? button-just-pressed?)
        (clear-interaction-state!)
        (update-paused! key-pressed? key-just-pressed?)
        (tick-game! audio skin stage textures world-viewport)
        (destroy-entities! audio skin stage textures)
        (handle-controls! stage world-viewport key-pressed? key-just-pressed?)
        (let [^Stage stage stage]
          (.act stage)
          (.draw stage))))

    (resize [_ width height]
      (.update (.getViewport ^Stage @stage) width height true)
      (.update ^Viewport @world-viewport width height false))

    (pause [_])

    (resume [_])))
