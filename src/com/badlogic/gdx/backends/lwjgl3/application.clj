(ns com.badlogic.gdx.backends.lwjgl3.application
  (:require [com.badlogic.gdx.backends.lwjgl3.config :as config])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [listener config]
  (config/use-glfw-async!)
  (Lwjgl3Application. listener
                      (config/create config)))
