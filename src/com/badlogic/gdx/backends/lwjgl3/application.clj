(ns com.badlogic.gdx.backends.lwjgl3.application
  (:require [com.badlogic.gdx.application-listener :as listener]
            [com.badlogic.gdx.backends.lwjgl3.config :as config])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create
  [{:keys [listener config]}]
  (Lwjgl3Application. (listener/create
                       (let [[f params] listener]
                         (f params)))
                      (config/create config)))
