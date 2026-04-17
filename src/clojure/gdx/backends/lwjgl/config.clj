(ns clojure.gdx.backends.lwjgl.config
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn use-glfw-async! []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync))

(defn create
  [{:keys [
           title
           window
           fps
           ]}]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle title)
    (.setWindowedMode (:width window) (:height window))
    (.setForegroundFPS fps)))
