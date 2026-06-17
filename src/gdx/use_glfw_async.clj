(ns gdx.use-glfw-async
  (:require [com.badlogic.gdx.utils.shared-library-loader :as shared-library-loader]
            [com.badlogic.gdx.utils.os :as os]
            [lwjgl.system.configuration :as configuration]))

(defn f! []
  (when (= (shared-library-loader/os) os/mac-os)
    (configuration/set-glfw-library-name! "glfw_async")))
