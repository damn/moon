(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn create []
  (Lwjgl3ApplicationConfiguration.))

(defn set-title! [^Lwjgl3ApplicationConfiguration config title]
  (.setTitle config title))

(defn set-windowed-mode! [^Lwjgl3ApplicationConfiguration config width height]
  (.setWindowedMode config width height))

(defn set-foreground-fps! [^Lwjgl3ApplicationConfiguration config fps]
  (.setForegroundFPS config fps))
