(ns clojure.gdx.backends.lwjgl.application.config
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.utils SharedLibraryLoader
                                   Os)
           (org.lwjgl.system Configuration)))

(defn use-glfw-async! []
  (when (= SharedLibraryLoader/os Os/MacOsX)
    (.set Configuration/GLFW_LIBRARY_NAME "glfw_async")))

(defn create
  [{:keys [title
           windowed-mode
           foreground-fps]}]
  (doto (Lwjgl3ApplicationConfiguration.)
    (.setTitle title)
    (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
    (.setForegroundFPS foreground-fps)))
