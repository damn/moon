(ns moon.application
  (:require [clojure.gdx :as gdx]
            [clojure.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl3.application.configuration :as configuration]
            gdl.audio
            gdl.audio.sound
            [moon.core :refer [call edn-resource]])
  (:import (com.badlogic.gdx Audio
                             ApplicationListener)
           (com.badlogic.gdx.audio Sound))
  (:gen-class))

(def state (atom nil))

(defn start! [config]
  (configuration/use-glfw-async!)
  (let [create!  (:create!  config)
        dispose! (:dispose! config)
        render!  (:render!  config)
        resize!  (:resize!  config)
        listener (reify ApplicationListener
                   (create [_]
                     (reset! state (reduce (fn [ctx [f & params]]
                                             (apply f ctx params))
                                           (gdx/context)
                                           create!)))

                   (dispose [_]
                     (dispose! @state))

                   (render [_]
                     (swap! state (fn [ctx]
                                    (reduce (fn [ctx [f & params]]
                                              (apply f ctx params))
                                            ctx
                                            render!))))

                   (resize [_ width height]
                     (resize! @state width height))

                   (pause [_])

                   (resume [_]))]
    (application/create listener (configuration/create config))))

(defn -main []
  (run! call (edn-resource "config.edn")))

(extend-type Audio
  gdl.audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Sound
  gdl.audio.sound/Sound
  (play! [this]
    (.play this)))
