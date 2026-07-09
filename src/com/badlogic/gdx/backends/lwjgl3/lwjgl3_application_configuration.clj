(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn set-title! [config title]
  (Lwjgl3ApplicationConfiguration/.setTitle config title))

(defn set-windowed-mode! [config {:keys [width height]}]
  (Lwjgl3ApplicationConfiguration/.setWindowedMode config (int width) (int height)))

(defn set-foreground-fps! [config foreground-fps]
  (Lwjgl3ApplicationConfiguration/.setForegroundFPS config (int foreground-fps)))

(def k->opts
  {
   :title set-title!
   :windowed-mode set-windowed-mode!
   :foreground-fps set-foreground-fps!
   })

(defn build [opts]
  (let [configuration (Lwjgl3ApplicationConfiguration.)]
    (doseq [[k v] opts]
      (let [apply! (k->opts k)]
        (assert apply! (str "Unknown lwjgl3 config option: " k))
        (apply! configuration v)))
    configuration))
