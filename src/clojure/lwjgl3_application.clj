(ns clojure.lwjgl3-application
  (:require [clojure.application-listener :as application-listener]
            [clojure.configuration :as configuration]
            [clojure.lwjgl3-application-configuration :as config]
            [clojure.shared-library-loader :as shared-library-loader]
            [clojure.os :as os])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn f [listener-callbacks config-opts]
  (when (= shared-library-loader/os os/mac-os-x)
    (configuration/set! configuration/glfw-library-name "glfw_async"))
  (Lwjgl3Application. (application-listener/create listener-callbacks)
                      (config/build config-opts)))
