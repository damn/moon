(ns moon.application
  (:require [clojure.gdx :as gdx]
            [clojure.gdx.colors :as colors]
            [moon.game :as game])
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
           create-params
           render-params
           ]}]
  (colors/put! colors)
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (reset! state (game/create! {:ctx/app       (gdx/app)
                                                       :ctx/audio     (gdx/audio)
                                                       :ctx/files     (gdx/files)
                                                       :ctx/graphics  (gdx/graphics)
                                                       :ctx/input     (gdx/input)}
                                                      create-params)))

                        (dispose [_]
                          (game/dispose! @state))

                        (render [_]
                          (swap! state game/render! render-params))

                        (resize [_ width height]
                          (game/resize! @state width height))

                        (pause [_])

                        (resume [_]))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width window) (:height window))
                        (.setForegroundFPS fps))))
