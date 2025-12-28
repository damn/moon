(ns gdl.application
  (:require [clojure.gdx :as gdx]
            [clojure.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl3.application.configuration :as configuration]
            gdl.audio
            gdl.audio.sound)
  (:import (com.badlogic.gdx Audio
                             ApplicationListener)
           (com.badlogic.gdx.audio Sound)))

(defprotocol Listener
  (create! [_ app])
  (dispose! [_])
  (render! [_])
  (resize! [_ width height])
  (pause! [_])
  (resume! [_]))

(defn start! [listener config]
  (configuration/use-glfw-async!)
  (application/create (reify ApplicationListener
                        (create [_]
                          (create! listener (gdx/context)))

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
                      (configuration/create config)))

(extend-type Audio
  gdl.audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Sound
  gdl.audio.sound/Sound
  (play! [this]
    (.play this)))
