(ns clojure.editor-start
  (:require [clojure.run-executions :refer [run-executions!]]))

(defn -main []
  (run-executions! "config/editor.edn"))
