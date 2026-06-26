(ns org.lwjgl.system.configuration
  (:import (org.lwjgl.system Configuration))
  (:refer-clojure :exclude [set!]))

(def glfw-library-name Configuration/GLFW_LIBRARY_NAME)

(defn set! [^Configuration configuration value]
  (.set configuration value))
