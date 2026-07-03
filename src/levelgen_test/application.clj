(ns levelgen-test.application
  (:require [clojure.gdx.application-listener.new :as create-listener]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            levelgen-test.application-listener
            levelgen-test.state))

(defn start!
  [config]
  (lwjgl3-application/f (create-listener/f
                         (levelgen-test.application-listener/f levelgen-test.state/application
                                                               config))
                        (create-config/f (:lwjgl-app-config config))))
