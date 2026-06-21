(ns moon.application.start
  (:require [clojure.backends.lwjgl.application :as application]
            [clojure.backends.lwjgl.application-config :as config]))

(defn f!
  [{:keys [listener]
    :as config}]
  (application/create (let [[f params] listener]
                        (f params))
                      (config/create config)))
