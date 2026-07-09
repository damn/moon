(ns clojure.lwjgl3-application
  (:require [clojure.application-listener :as application-listener])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.utils Os
                                   SharedLibraryLoader)
           (org.lwjgl.system Configuration)))

(defn- set-title! [config title]
  (Lwjgl3ApplicationConfiguration/.setTitle config title))

(defn- set-windowed-mode! [config width height]
  (Lwjgl3ApplicationConfiguration/.setWindowedMode config (int width) (int height)))

(defn- set-foreground-fps! [config foreground-fps]
  (Lwjgl3ApplicationConfiguration/.setForegroundFPS config (int foreground-fps)))

(def ^:private config-k->opts
  {:title set-title!
   :windowed-mode (fn [config {:keys [width height]}]
                    (set-windowed-mode! config width height))
   :foreground-fps set-foreground-fps!})

(defn- config [opts]
  (let [configuration (Lwjgl3ApplicationConfiguration.)]
    (doseq [[k v] opts]
      (let [apply! (config-k->opts k)]
        (assert apply! (str "Unknown lwjgl3 config option: " k))
        (apply! configuration v)))
    configuration))

(defn f [listener-callbacks config-opts]
  (when (= SharedLibraryLoader/os Os/MacOsX)
    (Configuration/.set Configuration/GLFW_LIBRARY_NAME "glfw_async"))
  (Lwjgl3Application. (application-listener/create listener-callbacks)
                      (config config-opts)))
