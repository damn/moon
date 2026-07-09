(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn create []
  (Lwjgl3ApplicationConfiguration.))

(defn set-title! [config title]
  (Lwjgl3ApplicationConfiguration/.setTitle config title))

(defn set-windowed-mode! [config {:keys [width height]}]
  (Lwjgl3ApplicationConfiguration/.setWindowedMode config (int width) (int height)))

(defn set-foreground-fps! [config foreground-fps]
  (Lwjgl3ApplicationConfiguration/.setForegroundFPS config (int foreground-fps)))
