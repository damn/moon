
; Aufgabe:
; complete externalize this application so we can understand it

; this is together because it is the complete application
; so we have an overview
; === pass constants per config ??? part of 'ctx' == 'state' ?
; == only functions !?
(ns levelgen-test.start
  (:require [clojure.edn :as edn]
            [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.use-glfw-async :as use-glfw-async!]
            [clojure.java.io :as io]
            levelgen-test.application-listener))

; TODO we want to know the application state actually .....
; => validate ?

(def state (atom nil))

(defn -main []
  (use-glfw-async!/f)
  (let [config (-> "config/levelgen_test.edn"
                   io/resource
                   slurp
                   edn/read-string)]
    (lwjgl3-application/f (create-listener/f
                           (levelgen-test.application-listener/f state
                                                                 config))
                          (create-config/f (:lwjgl-app-config config)))))
