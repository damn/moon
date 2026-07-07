(ns clojure.use-glfw-async
  (:require [clojure.os :as os]
            [clojure.shared-library-loader :as shared-library-loader]
            [clojure.configuration :as configuration]))

(defn f []
  (when (= shared-library-loader/os os/mac-os-x)
    (configuration/set! configuration/glfw-library-name "glfw_async")))
