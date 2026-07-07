(ns clojure.levelgen-test-start
  (:require [clojure.edn-resource :refer [edn-resource]]
            [clojure.levelgen-test-application]))

(defn -main []
  (-> "config/levelgen_test.edn"
      edn-resource
      clojure.levelgen-test-application/start!))
