(ns gdx.lwjgl3-application-configuration
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]))

(defn use-glfw-async! []
  (config/useGlfwAsync))

(def options
  {:title (fn [cfg title]
            (config/setTitle cfg title))
   :windowed-mode (fn [cfg [width height]]
                    (config/setWindowedMode cfg width height))
   :foreground-fps (fn [cfg fps]
                    (config/setForegroundFPS cfg fps))})

(defn create [opts]
  (let [cfg (config/new)]
    (doseq [[k v] opts :when (options k)]
      ((options k) cfg v))
    cfg))
