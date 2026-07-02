(ns clojure.gdx.use-glfw-async
  (:require [clojure.gdx.os.mac-os-x :as mac-os-x]
            [clojure.gdx.shared-library-loader.os :as os]
            [clojure.lwjgl.configuration.glfw-library-name :as glfw-library-name]
            [clojure.lwjgl.configuration.set! :as conf-set!]))

(defn f []
  (when (= os/v mac-os-x/v)
    (conf-set!/f glfw-library-name/v "glfw_async")))
