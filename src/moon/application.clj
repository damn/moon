(ns moon.application
  (:require [clojure.gdx.colors :as colors])
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(def state (atom nil))

(defn start!
  [{:keys [
           colors
           title
           window
           fps
           create!
           create-params
           dispose!
           render!
           render-params
           resize!
           ]}]
  (colors/put! colors)
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (reset! state (create! create-params)))

                        (dispose [_]
                          (dispose! @state))

                        (render [_]
                          (swap! state render! render-params))

                        (resize [_ width height]
                          (resize! @state width height))

                        (pause [_])

                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width window) (:height window))
                        (.setForegroundFPS fps))))
