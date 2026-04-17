(ns moon.application
  (:require [clojure.gdx.application-listener :as application-listener]
            [clojure.gdx.backends.lwjgl :as lwjgl]
            [clojure.gdx.backends.lwjgl.config :as config]
            [clojure.gdx.colors :as colors]))

(def state (atom nil))

(defn start! [{:keys [colors listener config]}]
  (colors/put! colors)
  (config/use-glfw-async!)
  (lwjgl/application! (application-listener/create listener)
                      (config/create config)))
