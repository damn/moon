(ns moon.application.start
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(defn f!
  [{:keys [listener title windowed-mode foreground-fps]}]
  (Lwjgl3Application. (let [[f params] listener]
                        (f params))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))
