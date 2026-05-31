(ns start
  (:require [clojure.core-ext :as core])
  (:gen-class))

(defn -main []
  (core/run-executions-from-edn! "config/start.edn"))
