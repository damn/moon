(ns clojure.gdx.backends.lwjgl3.lwjgl3-application
  (:require [com.badlogic.gdx.application :as application]
            [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]
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

      use-glfw-async!
      (fn []
        (when (= shared-library-loader/os os/MacOsX)
          (configuration/set configuration/GLFW_LIBRARY_NAME "glfw_async")))
      ]
  (defn create [application-listener
                config-opts]
    (use-glfw-async!) ; complecting !
    (lwjgl3/new (application-listener/new application-listener) ;< - dumb forward
                (build-config config-opts)))) ; <- dumb forward
