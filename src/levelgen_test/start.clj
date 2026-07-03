(ns levelgen-test.start
  (:require [clojure.edn :as edn]
            [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.use-glfw-async :as use-glfw-async!]
            [clojure.java.io :as io]
            levelgen-test.application-listener
            levelgen-test.state))

(defn -main []
  (use-glfw-async!/f)
  (let [config (-> "config/levelgen_test.edn"
                   io/resource
                   slurp
                   edn/read-string)]
    (lwjgl3-application/f (create-listener/f
                           (levelgen-test.application-listener/f levelgen-test.state/application
                                                                 config))
                          (create-config/f (:lwjgl-app-config config)))))
