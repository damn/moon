(ns moon.application
  (:require [moon.application.create :as create]
            [moon.application.dispose :as dispose]
            [moon.application.render :as render]
            [moon.application.resize :as resize])
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (reset! state (create/do!)))

                        (dispose [_]
                          (dispose/do! @state))

                        (render [_]
                          (swap! state render/do!))

                        (resize [_ width height]
                          (resize/do! @state width height))

                        (pause [_])

                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Cyber Dungeon Quest")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
