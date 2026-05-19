(ns moon.start
  (:require [clojure.config :refer [edn-resource]]
            [com.badlogic.gdx.gdx :as gdx])
  (:gen-class))

(defn -main []
  (gdx/start! (edn-resource "start.edn")))
