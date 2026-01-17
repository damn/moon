(ns moon.start.create-config
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn step [ctx {:keys [title window fps]}]
  (assoc ctx :app/config
         (doto (Lwjgl3ApplicationConfiguration.)
           (.setTitle title)
           (.setWindowedMode (:width window) (:height window))
           (.setForegroundFPS fps))))
