(ns clj.api.com.badlogic.gdx.backends.lwjgl3.application-configuration
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn use-glfw-async! []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync))

(defn create [config]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle (:title config))
    (.setWindowedMode (:width (:window config))
                      (:height (:window config)))
    (.setForegroundFPS (:fps config))))
