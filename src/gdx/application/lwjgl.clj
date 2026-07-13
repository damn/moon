(ns gdx.application.lwjgl
  (:require [gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]))

(defn use-glfw-async! []
  (config/useGlfwAsync))

(let [k->opts
      {
       :title          config/setTitle
       :windowed-mode  (fn [config {:keys [width height]}]
                                    (config/setWindowedMode config width height))
       :foreground-fps config/setForegroundFPS
       }]
  (defn- create-config [config-opts]
    (let [config (config/new)]
      (doseq [[k v] config-opts]
        (let [apply! (k->opts k)]
          (assert apply! (str "Unknown lwjgl3 config option: " k))
          (apply! config v)))
      config)))

(defn create
  [{:keys [config listener]}]
  (lwjgl3-application/new (application-listener/create listener)
                          (create-config config)))
