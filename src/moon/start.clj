(ns moon.start
  (:require [clojure.config :refer [edn-resource]]
            [com.badlogic.gdx.application-listener :as listener]
            [com.badlogic.gdx.backends.lwjgl3.application :as application]
            [com.badlogic.gdx.backends.lwjgl3.config :as config]
            moon.application-listener)
  (:gen-class))

(defn -main []
  (let [{:keys [listener
                config]} (edn-resource "game.edn")]
    (config/use-glfw-async!)
    (application/create (listener/create (moon.application-listener/create listener))
                        (config/create config))))
