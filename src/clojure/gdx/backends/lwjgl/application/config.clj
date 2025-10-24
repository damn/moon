(ns clojure.gdx.backends.lwjgl.application.config
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn create [{:keys [title window fps]}]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle title)
    (.setWindowedMode (:width window)
                      (:height window))
    (.setForegroundFPS fps)))

(defn display-mode
  "the currently active {@link DisplayMode} of the primary or given monitor"
  ([]
   (Lwjgl3ApplicationConfiguration/getDisplayMode))
  ([monitor]
   (Lwjgl3ApplicationConfiguration/getDisplayMode monitor)))

(defn display-modes
  "the available {@link DisplayMode}s of the given {@link Monitor} or of the primary montior."
  ([]
   (Lwjgl3ApplicationConfiguration/getDisplayModes))
  ([monitor]
   (Lwjgl3ApplicationConfiguration/getDisplayModes monitor)))

(defn primary-monitor
  "the primary {@link Monitor}"
  []
  (Lwjgl3ApplicationConfiguration/getPrimaryMonitor))

(defn monitors
  "the connected {@link Monitor}s"
  []
  (Lwjgl3ApplicationConfiguration/getMonitors))
