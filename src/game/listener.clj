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
                                 audio
                                 batch
                                 cursors
                                 default-font
                                 explored-tile-corners
                                 raycaster
                                 shape-drawer
                                 shape-drawer-texture
                                 skin
                                 stage
                                 start-position
                                 textures
                                 tiled-map
                                 unit-scale
                                 world
                                 world-mouse-position
                                 world-viewport]])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input InputProcessor)
           (com.badlogic.gdx.scenes.scene2d Actor Stage)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport Viewport)))

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
      (reset! world-viewport (create-world-viewport!))
      (reset! default-font (create-default-font! Gdx/files))
      (doseq [^Actor actor (create-ui-actors)]
        (.addActor ^Stage @stage actor))
      (let [{level-tiled-map :tiled-map
             level-start :start-position} (create-level! @textures)]
        (reset! tiled-map level-tiled-map)
        (reset! start-position level-start))
      (reset! world (create-world! @tiled-map))
      (reset! explored-tile-corners (create-explored-tile-corners! @tiled-map))
      (reset! raycaster (create-raycaster! @world))
      (spawn-player!)
      (bind-player-eid!)
      (spawn-map-creatures!))

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
            button-just-pressed? #(.isButtonJustPressed ^Input input (int %))]
        (ScreenUtils/clear 0 0 0 0)
        (update-mouseover-eid!)
        (update-active-entities!)
        (set-camera-to-player!)
        (draw-tiled-map!)
        (draw-world!)
        (assoc-interaction-state (current-mouseover-actor) (world-mouse-position))
        (update-cursor!)
        (handle-player-input! key-pressed? button-just-pressed?)
        (clear-interaction-state!)
        (update-paused! key-pressed? key-just-pressed?)
        (tick-game!)
        (destroy-entities!)
        (handle-controls! key-pressed? key-just-pressed?)
        (let [^Stage stage @stage]
          (.act stage)
          (.draw stage))))

    (resize [_ width height]
      (.update (.getViewport ^Stage @stage) width height true)
      (.update ^Viewport @world-viewport width height false))

    (pause [_])

    (resume [_])))
