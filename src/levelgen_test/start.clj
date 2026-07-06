(ns levelgen-test.start
  (:require [clojure.edn :as edn]
            [gdx.use-glfw-async :as use-glfw-async!]
            [clojure.java.io :as io]
            [levelgen-test.application]))

(defn -main []
  (use-glfw-async!/f)
  (-> "config/levelgen_test.edn"
      io/resource
      slurp
      edn/read-string
      levelgen-test.application/start!))
