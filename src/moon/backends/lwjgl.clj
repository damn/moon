(ns moon.backends.lwjgl
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)
           (com.badlogic.gdx.utils SharedLibraryLoader
                                   Os)
           (org.lwjgl.system Configuration)))

(defn use-glfw-async!
  "Whether to use the \"glfw_async\" library. This method only does something on mac operating system.
  This means you do not have to set the JVM argument \"-XstartOnFirstThread\"

  @see <a href= \"https://libgdx.com/news/2021/07/devlog-7-lwjgl3#do-i-need-to-do-anything-else\"> Documentation</a>"
  []
  (when (= SharedLibraryLoader/os Os/MacOsX)
    (.set Configuration/GLFW_LIBRARY_NAME "glfw_async")))

(defn application! [application-listener {:keys [title width height fps]}]
  (Lwjgl3Application. application-listener
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode width height)
                        (.setForegroundFPS fps))))
