(ns clojure.gdx.backends.lwjgl3.application.config
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)))

(defn use-glfw-async! []
  (Lwjgl3ApplicationConfiguration/useGlfwAsync))

(defn create
  [{:keys [title
           windowed-mode
           foreground-fps]}]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle title)
    (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
    (.setForegroundFPS foreground-fps)))
