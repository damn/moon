(ns moon.start
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl-app]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]
            [com.badlogic.gdx.utils.disposable :as disposable]

            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.utils.viewport :as viewport]

            [clojure.config :refer [edn-resource]]
            )
  (:gen-class))

(def state (atom nil))

(defn -main []
  (config/use-glfw-async!)
  (lwjgl-app/create (application-listener/create
                     {:create!
                      (fn []
                        (reset! state
                                (reduce (fn [ctx [f & params]]
                                          (apply f ctx params))
                                        {}
                                        (edn-resource "create.edn"))))

                      :dispose!
                      (fn []
                        (let [{:keys [ctx/audio
                                      ctx/batch
                                      ctx/cursors
                                      ctx/default-font
                                      ctx/shape-drawer-texture
                                      ctx/skin
                                      ctx/textures
                                      ctx/tiled-map]} @state]
                          (run! disposable/dispose! (vals audio))
                          (disposable/dispose! batch)
                          (run! disposable/dispose! (vals cursors))
                          (disposable/dispose! default-font)
                          (disposable/dispose! shape-drawer-texture)
                          (disposable/dispose! skin)
                          (run! disposable/dispose! (vals textures))
                          (disposable/dispose! tiled-map)))

                      :render!
                      (fn []
                        (swap! state
                               (fn [ctx]
                                 (reduce (fn [ctx [f & params]]
                                           (apply f ctx params))
                                         ctx
                                         (edn-resource "render.edn")))))

                      :resize!
                      (fn [width height]
                        (let [{:keys [ctx/stage
                                      ctx/world-viewport]} @state]
                          (viewport/update! (stage/viewport stage) width height true)
                          (viewport/update! world-viewport width height false)))

                      :pause!  (fn [])
                      :resume! (fn [])
                      })
                    (config/create
                     {:title "Moon"
                      :windowed-mode {:width 1440
                                      :height 900}
                      :foreground-fps 60})))
