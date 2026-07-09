(ns gdx.lwjgl3-application
  (:require [com.badlogic.gdx.application-listener :as listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as app]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]
            [com.badlogic.gdx.utils.shared-library-loader :as shared-library-loader]
            [com.badlogic.gdx.utils.os :as os]
            [org.lwjgl.system.configuration :as configuration]))

(let [k->opts
      {
       :config/set-title          config/set-title
       :config/set-windowed-mode  config/set-windowed-mode
       :config/set-foreground-fps config/set-foreground-fps
       }

      build-config
      (fn [config-opts]
        (let [config (config/new)]
          (doseq [[k v] config-opts]
            (let [apply! (k->opts k)]
              (assert apply! (str "Unknown lwjgl3 config option: " k))
              (apply! config v)))
          config))
      ]
  (defn create [listener
                config-opts]
    (when (= shared-library-loader/os os/mac-os-x)
      (configuration/set! configuration/glfw-library-name "glfw_async"))
    (app/new (listener/create listener)
             (build-config config-opts))))
