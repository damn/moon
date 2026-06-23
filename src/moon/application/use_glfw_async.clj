(ns moon.application.use-glfw-async
  (:require [utils.shared-library-loader :as shared-library-loader]
            [utils.os :as os]
            [lwjgl.configuration :as lwjgl]))

(defn f! []
  (when (= (shared-library-loader/os) os/mac-os)
    (lwjgl/set-glfw-library-name! "glfw_async")))
