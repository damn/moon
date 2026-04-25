(ns clojure.gdx.backends.lwjgl
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(defn application! [application-listener {:keys [title width height fps]}]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. application-listener
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode width height)
                        (.setForegroundFPS fps))))
