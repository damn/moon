(ns moon.backends.lwjgl.application.config
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn create [{:keys [title window fps]}]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle title)
    (.setWindowedMode (:width window)
                      (:height window))
    (.setForegroundFPS fps)))

(defn use-glfw-async! []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync))
