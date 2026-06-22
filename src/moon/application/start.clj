(ns moon.application.start
  (:require [gdl.application :as application]
            [gdl.application-config :as config]))

(defn f!
  [{:keys [listener]
    :as config}]
  (application/create (let [[f params] listener]
                        (f params))
                      (config/create config)))
