(ns clojure.use-glfw-async
  "Whether to use the \"glfw_async\" library. This method only does something on mac operating system.

  This means you do not have to set the JVM argument \"-XstartOnFirstThread\".

  See: `https://libgdx.com/news/2021/07/devlog-7-lwjgl3#do-i-need-to-do-anything-else`."
  (:import (com.badlogic.gdx.utils Os
                                   SharedLibraryLoader)
           (org.lwjgl.system Configuration)))

(defn f! []
  (when (= SharedLibraryLoader/os Os/MacOsX)
    (.set Configuration/GLFW_LIBRARY_NAME "glfw_async")))
