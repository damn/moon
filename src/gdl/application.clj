(ns gdl.application
  (:require gdl.audio
            gdl.audio.sound)
  (:import (com.badlogic.gdx Audio
                             ApplicationListener
                             Gdx)
           (com.badlogic.gdx.audio Sound)
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
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (create! listener {:ctx/audio    Gdx/audio
                                             :ctx/files    Gdx/files
                                             :ctx/graphics Gdx/graphics
                                             :ctx/input    Gdx/input}))

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

(extend-type Audio
  gdl.audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Sound
  gdl.audio.sound/Sound
  (play! [this]
    (.play this)))
