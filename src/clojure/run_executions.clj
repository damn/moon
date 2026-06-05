(ns clojure.run-executions
  (:require [clojure.edn-resource :refer [edn-resource]]))

(defn run-executions! [path]
  (doseq [[f & params] (edn-resource path)]
    (apply f params)))
