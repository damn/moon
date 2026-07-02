(ns levelgen-test.start
  (:require [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            [clojure.gdx.use-glfw-async :as use-glfw-async!]
            [clojure.gdx.color.float-bits :as float-bits]
            [clojure.gdx.orthographic-camera.new :as new-camera]
            [clojure.gdx.orthographic-camera.set-to-ortho :as set-to-ortho!]
            [gdx.scenes.scene2d.ui.window :as window]
            [scene2d.stage :as stage]
            [levelgen-test.create.edit-window :refer [edit-window]]
            [levelgen-test.generate-level :as generate-level]
            [clojure.gdx.fit-viewport.new :as fit-viewport]
            [moon.application.listener]
            [ctx.gdx-context]
            [map.assoc-create]
            [map.create]
            [ctx.db]
            [ctx.textures]
            [ctx.batch]
            [ctx.skin]
            [levelgen-test.dispose]
            [render.get-stage-ctx]
            [pipeline.do]
            [render.clear-screen]
            [levelgen-test.draw-tiled-map]
            [levelgen-test.camera-zoom-controls]
            [levelgen-test.camera-movement-controls]
            [render.update-draw-stage]
            [levelgen-test.resize])
  (:import (com.badlogic.gdx Input)
           (com.badlogic.gdx.scenes.scene2d Stage)))

(defn create!
  [{:keys [ctx/files
           ctx/input
           ctx/sprite-batch]
    :as ctx}]
  (let [initial-level-fn "config/world_fns/uf_caves.edn"
        level-fns ["config/world_fns/vampire.edn"
                   "config/world_fns/uf_caves.edn"
                   "config/world_fns/modules.edn"]
        ui-viewport (fit-viewport/create 1440 900)
        stage (stage/create ui-viewport sprite-batch)
        _  (.setInputProcessor ^Input input stage)
        tile-size 48
        world-unit-scale (float (/ tile-size))
        ctx (assoc ctx :ctx/stage stage)
        world-viewport (let [world-width  (* 1440 world-unit-scale)
                             world-height (* 900  world-unit-scale)]
                         (fit-viewport/create world-width
                                              world-height
                                              (doto (new-camera/f)
                                                (set-to-ortho!/f! false world-width world-height))))
        ctx (assoc ctx
                   :ctx/world-viewport world-viewport
                   :ctx/camera (:viewport/camera world-viewport)
                   :ctx/color-setter (constantly (float-bits/f [1 1 1 1]))
                   :ctx/zoom-speed 0.1
                   :ctx/camera-movement-speed 1
                   :ctx/world-unit-scale world-unit-scale)
        ctx (generate-level/f ctx initial-level-fn)]
    (Stage/.addActor (:ctx/stage ctx) (window/create (edit-window (:ctx/skin ctx) level-fns)))
    ctx))

(def state (atom nil))

(defn -main []
  (use-glfw-async!/f)
  (lwjgl3-application/f (create-listener/f
                         (moon.application.listener/f
                          {:state-var #'state
                           :create-pipeline [
                                             [ctx.gdx-context/f]
                                             [map.assoc-create/f :ctx/db ctx.db/step]
                                             [map.create/f :ctx/textures [ctx.textures/step
                                                                          {:folder "resources/"
                                                                           :extensions #{"png" "bmp"}}]]
                                             [map.assoc-create/f :ctx/sprite-batch ctx.batch/step]
                                             [map.assoc-create/f :ctx/skin ctx.skin/step]
                                             [create!]
                                             ]
                           :dispose! levelgen-test.dispose/f!
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
                           :resize! levelgen-test.resize/f!}))
                        (create-config/f
                         {:title "Levelgen Test"
                          :windowed-mode {:width 1440 :height 900}
                          :foreground-fps 60})))
