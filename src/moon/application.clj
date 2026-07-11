; Everything which depends on 'Gdx'
; is part of the application now

; moon.application.audio
; moon.application.files
; moon.application.graphics
; moon.application.input
; stage? batch? textures? sounds? world-viewport? shape-drawer?
; world-viewport owns camera
; stage owns ui-viewport owns that camera
; moon.application.stage.viewport.camera
; moon.application.world-viewport.camera
; pixmap, texture, texture-region, tiled-map is also plattform?
; files.file-handle too (files owns it)
; font generator too
; disposable?
(ns moon.application
  (:require [com.badlogic.gdx.application :as application]
            [com.badlogic.gdx.application-listener :as listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]
            [moon.gdx :as gdx]
            [com.badlogic.gdx.utils.shared-library-loader :as shared-library-loader]
            [com.badlogic.gdx.utils.os :as os]
            [org.lwjgl.system.configuration :as configuration]))

(defn get-audio [app]
  (application/getAudio app))

(defn get-files [app]
  (application/getFiles app))

(defn get-graphics [app]
  (application/getGraphics app))

(defn get-input [app]
  (application/getInput app))

(defn post-runnable! [app f]
  (application/postRunnable app f))

(let [k->opts
      {
       :config/set-title          config/setTitle
       :config/set-windowed-mode  (fn [config {:keys [width height]}]
                                    (config/setWindowedMode config width height))
       :config/set-foreground-fps config/setForegroundFPS
       }

      build-config
      (fn [config-opts]
        (let [config (config/new)]
          (doseq [[k v] config-opts]
            (let [apply! (k->opts k)]
              (assert apply! (str "Unknown lwjgl3 config option: " k))
              (apply! config v)))
          config))

      build-listener
      (fn [{:keys [create! dispose! render! resize!]}]
        (listener/new
         {:create! (fn []
                     (create! (gdx/app)))
          :dispose! dispose!
          :render! render!
          :resize! resize!
          :pause! (fn [])
          :resume! (fn [])}))

      use-glfw-async!
      (fn []
        (when (= shared-library-loader/os os/MacOsX)
          (configuration/set configuration/GLFW_LIBRARY_NAME "glfw_async")))
      ]
  (defn create [listener
                config-opts]
    (use-glfw-async!)
    (lwjgl3/new (build-listener listener)
                (build-config config-opts))))
