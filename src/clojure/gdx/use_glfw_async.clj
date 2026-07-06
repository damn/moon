(ns clojure.gdx.use-glfw-async
  (:require [clojure.gdx.os.mac-os-x :as mac-os-x]
            [clojure.gdx.shared-library-loader.os :as os]
            [org.lwjgl.system.configuration :as configuration]))

(defn f []
  (when (= os/v mac-os-x/v)
    (configuration/set! configuration/glfw-library-name "glfw_async")))
