(ns moon.application.use-glfw-async
  (:require [clojure.shared-library-loader :as shared-library-loader]
            [clojure.os :as os]
            [clojure.lwjgl :as lwjgl]))

(defn f! []
  (when (= (shared-library-loader/os) os/mac-os)
    (lwjgl/set-glfw-library-name! "glfw_async")))
