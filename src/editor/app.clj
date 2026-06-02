(ns editor.app
  (:require [clojure.core.run-executions :refer [run-executions!]]))

(def state (atom nil))

(defn -main []
  (run-executions! "config/editor.edn"))
