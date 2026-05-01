(ns moon.start
  (:require [clojure.gdx.backends.lwjgl.application :as lwjgl-app]
            [clojure.gdx.backends.lwjgl.application.config :as config]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.utils.viewport :as viewport]
            clojure.gdx.scene2d.ui.horizontal-group
            clojure.gdx.scene2d.ui.image-button
            clojure.gdx.scene2d.ui.scroll-pane
            clojure.gdx.scene2d.ui.stack
            clojure.gdx.scene2d.ui.text-button
            clojure.gdx.scene2d.ui.widget
            clojure.gdx.scene2d.ui.window
            moon.ui.data-viewer-window
            moon.ui.error-window
            moon.ui.property-editor-window
            moon.ui.property-overview-window

            [moon.application.create.gdx-colors :as gdx-colors]
            [moon.application.create.tooltip-config :as tooltip-config]
            [moon.application.create.batch :as create-batch]
            [moon.application.create.shape-drawer-texture :as create-shape-drawer-texture]
            [moon.application.create.shape-drawer :as create-shape-drawer]
            [moon.application.create.audio :as create-audio]
            [moon.application.create.default-font :as create-default-font]
            [moon.application.create.textures :as create-textures]
            [moon.application.create.world-unit-scale :as create-world-unit-scale]
            [moon.application.create.world-viewport :as create-world-viewport]
            [moon.application.create.cursors :as create-cursors]
            [moon.application.create.stage :as create-stage]
            [moon.application.create.skin :as create-skin]
            moon.application.create.unorganised
            moon.application.create.spawn-enemies
            moon.application.create.raycaster
            moon.application.create.spawn-player
            moon.application.create.grid
            moon.application.create.content-grid
            moon.application.create.explored-tile-corners
            moon.application.create.add-stage-actors
            moon.application.create.add-stage-actors.dev-menu
            moon.application.create.add-stage-actors.action-bar
            moon.application.create.add-stage-actors.hp-mana-bar
            moon.application.create.add-stage-actors.windows
            moon.application.create.add-stage-actors.player-state-draw
            moon.application.create.add-stage-actors.player-message
            moon.application.create.tiled-map
            moon.application.create.into-record
            moon.application.create.render-z-order
            moon.application.create.max-speed
            moon.application.create.db
            moon.application.create.ctx-colors
            moon.application.create.controls

            moon.application.render.if-not-paused
            moon.application.render.if-not-paused.tick-entities
            moon.application.render.if-not-paused.update-time
            moon.application.render.if-not-paused.update-potential-fields
            moon.application.render.handle-input
            moon.application.render.remove-destroyed-entities
            moon.application.render.set-cursor
            moon.application.render.assoc-paused
            moon.application.render.assoc-interaction-state
            moon.application.render.get-stage-ctx
            moon.application.render.draw-tiled-map
            moon.application.render.validate
            moon.application.render.update-mouse
            moon.application.render.update-mouseover-eid
            moon.application.render.draw-on-world-viewport
            moon.application.render.draw-on-world-viewport.draw-tile-grid
            moon.application.render.draw-on-world-viewport.draw-cell-debug
            moon.application.render.draw-on-world-viewport.draw-entities
            moon.application.render.draw-on-world-viewport.highlight-mouseover-tile
            moon.application.render.check-debug-viewer
            moon.application.render.active-entities
            moon.application.render.set-camera
            moon.application.render.clear-screen
            moon.application.render.window-camera-controls
            moon.application.render.stage
            )
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.utils Disposable))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (config/use-glfw-async!)
  (lwjgl-app/create (reify ApplicationListener
                      (create [_]
                        (reset! state
                                (reduce (fn [ctx [f & params]]
                                          (apply f ctx params))
                                        {:ctx/app       Gdx/app
                                         :ctx/graphics  Gdx/graphics
                                         :ctx/input     Gdx/input}
                                        [ ; TODO pass parameters ? edn-resource ?
                                         [gdx-colors/step]
                                         [tooltip-config/step]
                                         [create-batch/step]
                                         [create-shape-drawer-texture/step]
                                         [create-shape-drawer/step]
                                         [create-audio/step]
                                         [create-default-font/step]
                                         [create-textures/step]
                                         [create-world-unit-scale/step]
                                         [create-world-viewport/step]
                                         [create-cursors/step]
                                         [create-stage/step]
                                         [create-skin/step]
                                         [moon.application.create.unorganised/step] ; no deps
                                         [moon.application.create.controls/step] ; no deps
                                         [moon.application.create.ctx-colors/step] ; clojure.gdx.graphics.color
                                         [moon.application.create.render-z-order/step] ; moon.order
                                         [moon.application.create.max-speed/step] ; no deps
                                         [moon.application.create.db/step] ; moon.schemas, moon.property
                                         [moon.application.create.into-record/step] ;  no deps TODO make before spawn-player/enemies
                                         [moon.application.create.add-stage-actors/step [moon.application.create.add-stage-actors.dev-menu/create
                                                                                         moon.application.create.add-stage-actors.action-bar/create
                                                                                         moon.application.create.add-stage-actors.hp-mana-bar/create
                                                                                         moon.application.create.add-stage-actors.windows/create
                                                                                         moon.application.create.add-stage-actors.player-state-draw/create
                                                                                         moon.application.create.add-stage-actors.player-message/create]
                                          ] ; clojure.gdx.scene2d.stage
                                         [moon.application.create.tiled-map/step] ; FIXME
                                         [moon.application.create.grid/step]
                                         [moon.application.create.content-grid/step]
                                         [moon.application.create.explored-tile-corners/step]
                                         [moon.application.create.raycaster/step]
                                         [moon.application.create.spawn-player/step]
                                         [moon.application.create.spawn-enemies/step]
                                         ])))

                      (dispose [_]
                        ; TODO steps
                        (let [{:keys [ctx/audio
                                      ctx/batch
                                      ctx/cursors
                                      ctx/default-font
                                      ctx/shape-drawer-texture
                                      ctx/skin
                                      ctx/textures
                                      ctx/tiled-map]} @state]
                          (run! Disposable/.dispose (vals audio))
                          (Disposable/.dispose batch)
                          (run! Disposable/.dispose (vals cursors))
                          (Disposable/.dispose default-font)
                          (Disposable/.dispose shape-drawer-texture)
                          (Disposable/.dispose skin)
                          (run! Disposable/.dispose (vals textures))
                          (Disposable/.dispose tiled-map)))

                      (render [_]
                        (swap! state
                               (fn [ctx]
                                 (reduce (fn [ctx [f & params]]
                                           (apply f ctx params))
                                         ctx
                                         [[moon.application.render.get-stage-ctx/step]
                                          [moon.application.render.validate/step]
                                          [moon.application.render.update-mouse/step]
                                          [moon.application.render.update-mouseover-eid/step]
                                          [moon.application.render.check-debug-viewer/step]
                                          [moon.application.render.active-entities/step]
                                          [moon.application.render.set-camera/step]
                                          [moon.application.render.clear-screen/step]
                                          [moon.application.render.draw-tiled-map/step]
                                          [moon.application.render.draw-on-world-viewport/step [
                                                                                                #_moon.application.render.draw-on-world-viewport.draw-tile-grid/draws
                                                                                                moon.application.render.draw-on-world-viewport.draw-cell-debug/draws
                                                                                                moon.application.render.draw-on-world-viewport.draw-entities/do!
                                                                                                #_moon.geom-test
                                                                                                moon.application.render.draw-on-world-viewport.highlight-mouseover-tile/draws
                                                                                                ]]
                                          [moon.application.render.assoc-interaction-state/step]
                                          [moon.application.render.set-cursor/step]
                                          [moon.application.render.handle-input/step]
                                          [#(dissoc % :ctx/interaction-state)]
                                          [moon.application.render.assoc-paused/step]
                                          [moon.application.render.if-not-paused/step [moon.application.render.if-not-paused.update-time/step
                                                                                       moon.application.render.if-not-paused.update-potential-fields/step
                                                                                       moon.application.render.if-not-paused.tick-entities/step]]
                                          [moon.application.render.remove-destroyed-entities/step]
                                          [moon.application.render.window-camera-controls/step]
                                          [moon.application.render.stage/step]
                                          [moon.application.render.validate/step]]))))

                      (resize [_ width height]
                        ; TODO steps ?
                        (let [{:keys [ctx/stage
                                      ctx/world-viewport]} @state]
                          (viewport/update! (stage/viewport stage) width height true)
                          (viewport/update! world-viewport width height false)))

                      (pause [_])

                      (resume [_]))
                    (config/create
                     {:title "Moon"
                      :windowed-mode {:width 1440
                                      :height 900}
                      :foreground-fps 60})))
