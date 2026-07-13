(ns gdx.config
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl-config]))

(defn use-glfw-async! []
  (lwjgl-config/useGlfwAsync))

(def ^:private option->apply
  {:title          lwjgl-config/setTitle
   :windowed-mode  (fn [cfg {:keys [width height]}]
                     (lwjgl-config/setWindowedMode cfg width height))
   :foreground-fps lwjgl-config/setForegroundFPS})

(defn- apply-options! [cfg opts]
  (doseq [[k v] opts]
    (let [apply! (option->apply k)]
      (assert apply! (str "Unknown config option: " k))
      (apply! cfg v)))
  cfg)

(defn create [opts]
  (apply-options! (lwjgl-config/new) opts))
