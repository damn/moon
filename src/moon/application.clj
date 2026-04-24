(ns moon.application
  (:require [clojure.gdx.colors :as colors])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(def state (atom nil))

(defn start!
  [{:keys [
           colors
           title
           window
           fps
           listener
           ]}]
  (colors/put! colors)
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (let [[f params] listener]
                        (f params))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width window) (:height window))
                        (.setForegroundFPS fps))))
