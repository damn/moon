(ns clojure.use-glfw-async
  (:require [clojure.os :as os]
            [clojure.shared-library-loader :as shared-library-loader]
            [clojure.set! :as set!]
            [clojure.glfw-library-name :refer [glfw-library-name]]))

(defn f []
  (when (= shared-library-loader/os os/mac-os-x)
    (set!/f glfw-library-name "glfw_async")))
