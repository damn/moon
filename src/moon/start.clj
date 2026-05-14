(ns moon.start
  (:require [clojure.config :refer [edn-resource]]
            [com.badlogic.gdx.backends.lwjgl3.application :as application]
            [com.badlogic.gdx.backends.lwjgl3.config :as config]
            [moon.listener :as listener])
  (:gen-class))

(defn -main []
  (let [{:keys [listener
                config]} (edn-resource "start.edn")]
    (config/use-glfw-async!)
    (application/create (listener/create listener)
                        (config/create config))))
