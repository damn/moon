(ns levelgen-test.app
  (:require [clojure.run-executions :refer [run-executions!]]))

(def state (atom nil))

(defn -main []
  (run-executions! "config/levelgen-test.edn"))
