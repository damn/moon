(ns moon.start
  (:require [clojure.config :refer [edn-resource]])
  (:gen-class))

(defn -main []
  (doseq [[f & params] (edn-resource "start.edn")]
    (apply f params)))
