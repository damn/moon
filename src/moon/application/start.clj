(ns moon.application.start
  (:require [com.badlogic.gdx.backends.lwjgl.application :as application]
            [com.badlogic.gdx.backends.lwjgl.application-config :as config]))

(defn f!
  [{:keys [listener]
    :as config}]
  (application/create (let [[f params] listener]
                        (f params))
                      (config/create config)))
