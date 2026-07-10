(ns org.lwjgl.system.configuration
  (:refer-clojure :exclude [set])
  (:import (org.lwjgl.system Configuration)))

(def GLFW_LIBRARY_NAME Configuration/GLFW_LIBRARY_NAME)

(defn set [^Configuration configuration value]
  (.set configuration value))
