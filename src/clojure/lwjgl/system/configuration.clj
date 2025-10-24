(ns clojure.lwjgl.system.configuration
  (:import (org.lwjgl.system Configuration)))

(defn set-glfw-library-name! [string]
  (.set Configuration/GLFW_LIBRARY_NAME string))
