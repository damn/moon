(ns moon.start
  (:require [moon.application :as application])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

; TODO rewrite so far as to pass state (Gdx) as it doesn't fit here.
(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. application/listener
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Cyber Dungeon Quest")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
