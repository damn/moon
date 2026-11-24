(ns clojure.com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn create []
  (Lwjgl3ApplicationConfiguration.))

(defn use-glfw-async!
  "Whether to use the \"glfw_async\" library. This method only does something on mac operating system.
  
  This means you do not have to set the JVM argument \"-XstartOnFirstThread\""
  []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync))

(defn set-title!
  "Sets the window title. If null, the application listener's class name is used."
  [^Lwjgl3ApplicationConfiguration config title]
  (.setTitle config title))

(defn set-windowed-mode!
  "Sets the app to use windowed mode.
  
  - `width`: the width of the window (default 640)
  
  - `height`: the height of the window (default 480)"
  [^Lwjgl3ApplicationConfiguration config width height]
  (.setWindowedMode config width height))

(defn set-foreground-fps!
  "Sets the target framerate for the application. The CPU sleeps as needed. Must be positive. Use 0 to never sleep. Default is
  0."
  [^Lwjgl3ApplicationConfiguration config fps]
  (.setForegroundFPS config fps))
