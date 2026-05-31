(ns start
  (:require [clojure.core-ext :refer [edn-resource]]
            [gdx.backends.lwjgl :as lwjgl])
  (:gen-class))

(defn -main []
  (run! require (edn-resource "requires.edn"))
  (-> "start.edn"
      edn-resource
      lwjgl/application!))
