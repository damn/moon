(ns game.listener
  (:require [game.listener.create :refer [add-ui-actors!
                                          bind-player-eid!
                                          create-audio!
                                          create-batch!
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
                                          init-shape-drawer-texture!
                                          init-tooltip-manager!
                                          init-unit-scale!
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
                                 shape-drawer-texture
                                 skin
                                 stage
                                 textures
                                 tiled-map
                                 world-mouse-position
                                 world-viewport]])
  (:import (com.badlogic.gdx ApplicationListener Gdx Input)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.utils Disposable ScreenUtils)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def listener
  (reify ApplicationListener
    (create [_]
      (create-audio! Gdx/audio Gdx/files)
      (create-batch!)
      (init-unit-scale!)
      (init-shape-drawer-texture!)
      (create-shape-drawer!)
      (create-skin! Gdx/files)
      (create-stage! Gdx/input)
      (init-tooltip-manager!)
      (put-pretty-name-color!)
      (create-cursors! Gdx/files)
      (create-textures! Gdx/files)
      (create-world-viewport!)
      (create-default-font! Gdx/files)
      (add-ui-actors!)
      (create-level!)
      (create-world!)
      (create-explored-tile-corners!)
      (create-raycaster!)
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
