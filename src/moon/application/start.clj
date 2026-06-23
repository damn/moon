(ns moon.application.start
  (:require [lwjgl.application :as application]
            [lwjgl.application-config :as config]))

(defn f!
  [{:keys [listener]
    :as config}]
  (application/create (let [[f params] listener]
                        (f params))
                      (config/create config)))
