(ns moon.application.use-glfw-async
  (:require [clojure.shared-library-loader :as shared-library-loader]
            [clojure.os :as os]
            [lwjgl.system.configuration :as configuration]))

(defn f! []
  (when (= (shared-library-loader/os) os/mac-os)
    (configuration/set-glfw-library-name! "glfw_async")))
