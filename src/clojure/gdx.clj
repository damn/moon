(ns clojure.gdx
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(defn application!
  [{:keys [title
           windowed-mode
           foreground-fps
           create!
           dispose!
           render!
           resize!
           pause!
           resume!]}]
  (->> "clojure.gdx.edn"
       io/resource
       slurp
       edn/read-string
       (run! require))
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (create!))

                        (dispose [_]
                          (dispose!))

                        (render [_]
                          (render!))

                        (resize [_ width height]
                          (resize! width height))

                        (pause [_]
                          (pause!))

                        (resume [_]
                          (resume!)))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))
