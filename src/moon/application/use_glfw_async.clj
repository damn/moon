(ns moon.application.use-glfw-async
  (:require [com.badlogic.gdx.utils.shared-library-loader :as shared-library-loader]
            [com.badlogic.gdx.utils.os :as os]
            [lwjgl.configuration :as lwjgl]))

(defn f! []
  (when (= shared-library-loader/os os/mac-os-x)
    (lwjgl/set-glfw-library-name! "glfw_async")))
