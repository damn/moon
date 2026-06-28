(ns moon.application.use-glfw-async
  (:import (com.badlogic.gdx.utils Os
                                    SharedLibraryLoader)
           (org.lwjgl.system Configuration)))

(defn f! []
  (when (= SharedLibraryLoader/os Os/MacOsX)
    (.set Configuration/GLFW_LIBRARY_NAME "glfw_async")))
