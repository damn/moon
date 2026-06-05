(ns clojure.lwjgl.application
  (:require [clojure.gdx.application-listener :as listener])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(defn start!
  [{:keys [title windowed-mode foreground-fps]
    :as config}]
  (Lwjgl3Application. (listener/create config)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))
