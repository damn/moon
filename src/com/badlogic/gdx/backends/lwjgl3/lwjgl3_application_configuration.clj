(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn new []
  (Lwjgl3ApplicationConfiguration.))

(defn setTitle [config title]
  (.setTitle ^Lwjgl3ApplicationConfiguration config title))

(defn setWindowedMode [config width height]
  (.setWindowedMode ^Lwjgl3ApplicationConfiguration config (int width) (int height)))

(defn setForegroundFPS [config foreground-fps]
  (.setForegroundFPS ^Lwjgl3ApplicationConfiguration config (int foreground-fps)))
