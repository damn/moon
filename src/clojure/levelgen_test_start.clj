(ns clojure.levelgen-test-start
  (:require [clojure.edn :as edn]
            [clojure.use-glfw-async :as use-glfw-async!]
            [clojure.java.io :as io]
            [clojure.levelgen-test-application]))

(defn -main []
  (use-glfw-async!/f)
  (-> "config/levelgen_test.edn"
      io/resource
      slurp
      edn/read-string
      clojure.levelgen-test-application/start!))
