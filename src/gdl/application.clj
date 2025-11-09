(ns gdl.application
  (:import (com.badlogic.gdx ApplicationListener
                             Gdx)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(defprotocol Listener
  (create! [_ app])
  (dispose! [_])
  (render! [_])
  (resize! [_ width height])
  (pause! [_])
  (resume! [_]))

(defn start! [listener config]
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (create! listener Gdx/app))

                        (dispose [_]
                          (dispose! listener))

                        (render [_]
                          (render! listener))

                        (resize [_ width height]
                          (resize! listener width height))

                        (pause [_]
                          (pause! listener))

                        (resume [_]
                          (resume! listener)))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle (:title config))
                        (.setWindowedMode (:width (:window config))
                                          (:height (:window config)))
                        (.setForegroundFPS (:fps config)))))
