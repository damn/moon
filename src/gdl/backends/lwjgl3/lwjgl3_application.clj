; TODO 'gdl.start' ???
; => stuff could be clojure' protoocls ' all implemented by gdl.start ???

; start could be the only one holding com.badlogic.gdx
; - everything else could be clojure protocols???
; - we always create a stage/sprite-batch/ ???
; - ?!
; --- TOO BIG LAYER !!!! WHAT ARE WE ACTUALLY COMPOSING
; ACTUALLY WE ARE JUST COLLECTING FUNCTIONS FOR COM BADLOGIC GDX AGAIN

; ETF

(ns gdl.backends.lwjgl3.lwjgl3-application
  (:require [com.badlogic.gdx.application-listener :as listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as app]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]
            [com.badlogic.gdx.gdx :as gdx]
            [com.badlogic.gdx.utils.shared-library-loader :as shared-library-loader]
            [com.badlogic.gdx.utils.os :as os]
            [org.lwjgl.system.configuration :as configuration]))

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
    (app/new (build-listener listener)
             (build-config config-opts))))
