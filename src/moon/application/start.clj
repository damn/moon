(ns moon.application.start
  (:require [clojure.gdx.lwjgl3-application.new :as lwjgl3-application])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.utils Os
                                   SharedLibraryLoader)
           (org.lwjgl.system Configuration)))

(defn f!
  [{:keys [listener title windowed-mode foreground-fps]}]
  (when (= SharedLibraryLoader/os Os/MacOsX)
    (.set Configuration/GLFW_LIBRARY_NAME "glfw_async"))
  (lwjgl3-application/f (let [[f params] listener]
                          (f params))
                        (doto (Lwjgl3ApplicationConfiguration.)
                          (.setTitle title)
                          (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                          (.setForegroundFPS foreground-fps))))
