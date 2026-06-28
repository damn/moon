(ns moon.application.use-glfw-async
  (:require [org.lwjgl.system.configuration :as configuration])
  (:import (com.badlogic.gdx.utils Os
                                    SharedLibraryLoader)))

(defn f! []
  (when (= SharedLibraryLoader/os Os/MacOsX)
    (configuration/set! configuration/glfw-library-name "glfw_async")))
