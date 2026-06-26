(ns moon.application.use-glfw-async
  (:require [com.badlogic.gdx.utils.shared-library-loader :as shared-library-loader]
            [com.badlogic.gdx.utils.os :as os]
            [org.lwjgl.system.configuration :as configuration]))

(defn f! []
  (when (= shared-library-loader/os os/mac-os-x)
    (configuration/set! configuration/glfw-library-name "glfw_async")))
