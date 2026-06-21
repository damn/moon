(ns levelgen-test.start
  (:require [clojure.run-executions :refer [run-executions!]]))

(defn -main []
  (run-executions! "config/levelgen-test.edn"))
