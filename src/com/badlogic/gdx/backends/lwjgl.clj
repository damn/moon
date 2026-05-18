(ns com.badlogic.gdx.backends.lwjgl
  (:require [com.badlogic.gdx.application-listener :as listener]
            [com.badlogic.gdx.backends.lwjgl3.application :as application]
            [com.badlogic.gdx.backends.lwjgl3.config :as config]))

(def use-glfw-async! config/use-glfw-async!)

(defn application!
  [{:keys [listener config]}]
  (application/create (listener/create
                       (let [[f params] listener]
                         (f params)))
                      (config/create config)))
