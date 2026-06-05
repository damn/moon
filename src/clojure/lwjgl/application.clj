(ns clojure.lwjgl.application
  (:require [clojure.application-listener :as listener]
            [gdx.backends.lwjgl.application :as application]
            [gdx.backends.lwjgl.application-config :as config]))

(defn start!
  [config]
  (config/use-glfw-async!)
  (application/create (listener/create config)
                      (config/create config)))
