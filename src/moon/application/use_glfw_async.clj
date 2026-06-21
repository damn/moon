(ns moon.application.use-glfw-async
  (:require [clojure.utils.shared-library-loader :as shared-library-loader]
            [clojure.utils.os :as os]
            [lwjgl.system.configuration :as configuration]))

(defn f! []
  (when (= (shared-library-loader/os) os/mac-os)
    (configuration/set-glfw-library-name! "glfw_async")))
