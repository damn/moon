(ns moon.start
  (:require [moon.game :as game]
            [moon.game.dispose :as dispose]
            [moon.game.resize :as resize]
            )
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (reset! state (game/create!)))

                        (dispose [_]
                          (dispose/do! @state))

                        (render [_]
                          (swap! state game/render!))

                        (resize [_ width height]
                          (resize/do! @state width height))

                        (pause [_])

                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle "Moon")
                        (.setWindowedMode 1440 900)
                        (.setForegroundFPS 60))))
