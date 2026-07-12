(ns clojure.gdx.backends.lwjgl3.lwjgl3-application-configuration
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]))

(defn use-glfw-async! []
  (config/useGlfwAsync))

(let [k->opts
      {
       :config/set-title          config/setTitle
       :config/set-windowed-mode  (fn [config {:keys [width height]}]
                                    (config/setWindowedMode config width height))
       :config/set-foreground-fps config/setForegroundFPS
       }]
  (defn create [config-opts]
    (let [config (config/new)]
      (doseq [[k v] config-opts]
        (let [apply! (k->opts k)]
          (assert apply! (str "Unknown lwjgl3 config option: " k))
          (apply! config v)))
      config)))
