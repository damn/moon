(ns moon.application
  (:require [moon.application.create :as create]
            [moon.application.dispose :as dispose]
            [moon.application.render :as render]
            [moon.application.resize :as resize])
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.graphics Color
                                      Colors)
           (com.badlogic.gdx.scenes.scene2d.ui TooltipManager))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (doseq [[name [r g b a]]
          {
           "PRETTY_NAME" [0.84 0.8 0.52 1]
           }]
    (Colors/put name (Color. r g b a)))
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (set! (.initialTime (TooltipManager/getInstance)) 0)
                          (reset! state (create/do! {:ctx/audio     Gdx/audio
                                                     :ctx/files     Gdx/files
                                                     :ctx/graphics  Gdx/graphics
                                                     :ctx/input     Gdx/input})))

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
