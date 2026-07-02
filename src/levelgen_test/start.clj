(ns levelgen-test.start
  (:require [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            [clojure.gdx.use-glfw-async :as use-glfw-async!]
            [clojure.gdx.color.float-bits :as float-bits]
            [clojure.gdx.orthographic-camera.new :as new-camera]
            [clojure.gdx.orthographic-camera.set-to-ortho :as set-to-ortho!]
            [moon.db :as db]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.stage :as stage]
            [levelgen-test.create.edit-window :refer [edit-window]]
            [levelgen-test.generate-level :as generate-level]
            [clojure.gdx.fit-viewport.new :as fit-viewport]
            [moon.application.listener]
            [ctx.textures]
            [render.get-stage-ctx]
            [pipeline.do]
            [render.clear-screen]
            [levelgen-test.draw-tiled-map]
            [levelgen-test.camera-zoom-controls]
            [levelgen-test.camera-movement-controls]
            [render.update-draw-stage])
  (:import (com.badlogic.gdx Input
                             Files
                             Gdx)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)
           (com.badlogic.gdx.utils Disposable)
           (com.badlogic.gdx.utils.viewport Viewport)))

(def initial-level-fn "config/world_fns/uf_caves.edn")

(def level-fns ["config/world_fns/vampire.edn"
                "config/world_fns/uf_caves.edn"
                "config/world_fns/modules.edn"])

(def ui-viewport-width 1440)
(def ui-viewport-height 900)

(def tile-size 48)

(defn create!
  [_ctx]
  (let [files Gdx/files
        input Gdx/input
        graphics Gdx/graphics
        sprite-batch (SpriteBatch.)
        ui-viewport (fit-viewport/create ui-viewport-width ui-viewport-height)
        stage (stage/create ui-viewport sprite-batch)
        skin (Skin. (Files/.internal files "skin/uiskin.json"))
        world-unit-scale (float (/ tile-size))
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (doto (new-camera/f)
                                                (set-to-ortho!/f! false world-width world-height))))
        ctx {:ctx/files files
             :ctx/input input
             :ctx/graphics graphics
             :ctx/stage stage
             :ctx/db (db/create)
             :ctx/textures (ctx.textures/step {:ctx/files files}
                                              {:folder "resources/"
                                               :extensions #{"png" "bmp"}})
             :ctx/sprite-batch sprite-batch
             :ctx/skin skin
             :ctx/world-viewport world-viewport
             :ctx/camera (:viewport/camera world-viewport)
             :ctx/color-setter (constantly (float-bits/f [1 1 1 1]))
             :ctx/zoom-speed 0.1
             :ctx/camera-movement-speed 1
             :ctx/world-unit-scale world-unit-scale}
        ctx (generate-level/f ctx initial-level-fn)]
    (.setInputProcessor ^Input input stage)
    (Stage/.addActor (:ctx/stage ctx)
                     (window/create (edit-window skin level-fns)))
    ctx))

(defn dispose!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (Disposable/.dispose skin)
  (Disposable/.dispose sprite-batch)
  (Disposable/.dispose tiled-map))

(defn resize!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (.update ^Viewport (:stage/viewport stage) width height true)
  (.update ^Viewport world-viewport width height false))

(def state (atom nil))

(defn -main []
  (use-glfw-async!/f)
  (lwjgl3-application/f (create-listener/f
                         (moon.application.listener/f
                          {:state-var #'state
                           :create-pipeline [[create!]]
                           :dispose! dispose!
                           :render-pipeline [[render.get-stage-ctx/step]
                                             [pipeline.do/step
                                              [render.clear-screen/step]]
                                             [pipeline.do/step
                                              [levelgen-test.draw-tiled-map/f]]
                                             [pipeline.do/step
                                              [levelgen-test.camera-zoom-controls/f]]
                                             [pipeline.do/step
                                              [levelgen-test.camera-movement-controls/f]]
                                             [render.update-draw-stage/step]]
                           :resize! resize!}))
                        (create-config/f
                         {:title "Levelgen Test"
                          :windowed-mode {:width 1440 :height 900}
                          :foreground-fps 60})))
