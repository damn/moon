(ns com.badlogic.gdx.backends.lwjgl3.application
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.application-configuration :as config])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [listener config]
  (config/use-glfw-async!)
  (Lwjgl3Application. (application-listener/create listener)
                      (config/create config)))
