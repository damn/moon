(ns levelgen-test.start
  (:require [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            [clojure.gdx.use-glfw-async :as use-glfw-async!]
            [moon.application.listener]
            [ctx.gdx-context]
            [map.assoc-create]
            [map.create]
            [levelgen-test.create]
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
            [levelgen-test.resize]
            ))

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
                                             [levelgen-test.create/f!]
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
