(ns moon.application.start
  (:require [clojure.lwjgl.application :as application]
            [clojure.lwjgl.application-config :as config]))

(defn f!
  [{:keys [listener]
    :as config}]
  (application/create (let [[f params] listener]
                        (f params))
                      (config/create config)))
