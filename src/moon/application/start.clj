(ns moon.application.start
  (:require [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            [clojure.gdx.os.mac-os-x :as mac-os-x]
            [clojure.gdx.shared-library-loader.os :as os]
            [clojure.lwjgl.configuration.glfw-library-name :as glfw-library-name]
            [clojure.lwjgl.configuration.set! :as conf-set!]))

(defn f!
  [{:keys [listener] :as config}]
  (when (= os/v mac-os-x/v)
    (conf-set!/f glfw-library-name/v "glfw_async"))
  (lwjgl3-application/f (let [[f params] listener]
                          (f params))
                        (create-config/f config)))
