(ns clojure.lwjgl3-application-configuration
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn new []
  (Lwjgl3ApplicationConfiguration.))

(defn set-title! [config title]
  (Lwjgl3ApplicationConfiguration/.setTitle config title))

(defn set-windowed-mode! [config width height]
  (Lwjgl3ApplicationConfiguration/.setWindowedMode config (int width) (int height)))

(defn set-foreground-fps! [config foreground-fps]
  (Lwjgl3ApplicationConfiguration/.setForegroundFPS config (int foreground-fps)))

(def ^:private k->opts
  {:title set-title!
   :windowed-mode (fn [config {:keys [width height]}]
                    (set-windowed-mode! config width height))
   :foreground-fps set-foreground-fps!})

(defn f [opts]
  (let [config (Lwjgl3ApplicationConfiguration.)]
    (doseq [[k v] opts]
      (let [apply! (k->opts k)]
        (assert apply! (str "Unknown lwjgl3 config option: " k))
        (apply! config v)))
    config))
