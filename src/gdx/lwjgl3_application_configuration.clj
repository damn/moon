(ns gdx.lwjgl3-application-configuration
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration]))

(defn create []
  (lwjgl3-application-configuration/new))

(defn use-glfw-async! []
  (lwjgl3-application-configuration/useGlfwAsync))

(defn set-title! [cfg title]
  (lwjgl3-application-configuration/setTitle cfg title))

(defn set-windowed-mode! [cfg width height]
  (lwjgl3-application-configuration/setWindowedMode cfg width height))

(defn set-foreground-fps! [cfg fps]
  (lwjgl3-application-configuration/setForegroundFPS cfg fps))
