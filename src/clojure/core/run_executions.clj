(ns clojure.core.run-executions
  (:require [clojure.core.edn-resource :refer [edn-resource]]))

(defn run-executions! [path]
  (doseq [[f & params] (edn-resource path)]
    (apply f params)))
