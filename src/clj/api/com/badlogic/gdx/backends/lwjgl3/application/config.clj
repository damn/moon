(ns clj.api.com.badlogic.gdx.backends.lwjgl3.application.config
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn use-glfw-async! []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync))

(defn create []
  (Lwjgl3ApplicationConfiguration.))

(defn set-title! [^Lwjgl3ApplicationConfiguration config title]
  (.setTitle config title))

(defn set-windowed-mode! [^Lwjgl3ApplicationConfiguration config {:keys [width height]}]
  (.setWindowedMode config width height))

(defn set-foreground-fps! [^Lwjgl3ApplicationConfiguration config fps]
  (.setForegroundFPS config fps))
