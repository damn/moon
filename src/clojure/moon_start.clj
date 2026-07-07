(ns clojure.moon-start
  (:require [clojure.run-executions :refer [run-executions!]])
  (:gen-class))

(defn -main []
  (run-executions! "config/start.edn"))
