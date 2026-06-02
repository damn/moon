(ns start
  (:require [clojure.core.run-executions :refer [run-executions!]])
  (:gen-class))

(defn -main []
  (run-executions! "config/start.edn"))
