(ns moon.application.use-glfw-async
  (:require [gdl.shared-library-loader :as shared-library-loader]
            [gdl.os :as os]
            [lwjgl.configuration :as lwjgl]))

(defn f! []
  (when (= (shared-library-loader/os) os/mac-os)
    (lwjgl/set-glfw-library-name! "glfw_async")))
