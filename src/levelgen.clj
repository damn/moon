(ns levelgen
  (:require [levelgen.listener :refer [listener]])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application Lwjgl3ApplicationConfiguration)))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (listener)
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Levelgen Test")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
