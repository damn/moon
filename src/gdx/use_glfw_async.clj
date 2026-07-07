(ns gdx.use-glfw-async
  (:require [clojure.os :as os]
            [clojure.shared-library-loader :as shared-library-loader]
            [org.lwjgl.system.configuration :as configuration]))

(defn f []
  (when (= shared-library-loader/os os/mac-os-x)
    (configuration/set! configuration/glfw-library-name "glfw_async")))
