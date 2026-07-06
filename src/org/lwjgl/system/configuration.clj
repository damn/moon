(ns org.lwjgl.system.configuration
  (:refer-clojure :exclude [set!])
  (:import (org.lwjgl.system Configuration)))

(def glfw-library-name Configuration/GLFW_LIBRARY_NAME)

(defn set! [configuration value]
  (Configuration/.set configuration value))
