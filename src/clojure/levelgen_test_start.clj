(ns clojure.levelgen-test-start
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.levelgen-test-application]))

(defn -main []
  (-> "config/levelgen_test.edn"
      io/resource
      slurp
      edn/read-string
      clojure.levelgen-test-application/start!))
