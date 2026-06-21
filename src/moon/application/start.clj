(ns moon.application.start
  (:require [clojure.application :as application]
            [clojure.application-config :as config]))

(defn f!
  [{:keys [listener]
    :as config}]
  (application/create (let [[f params] listener]
                        (f params))
                      (config/create config)))
