(ns levelgen-test.application
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [clojure.gdx.lwjgl3-application.new :as lwjgl3-application]
            [clojure.gdx.lwjgl3-application-configuration.new :as create-config]
            levelgen-test.application-listener
            levelgen-test.state))

(defn start!
  [config]
  (lwjgl3-application/f (application-listener/new
                         (levelgen-test.application-listener/f levelgen-test.state/application
                                                               config))
                        (create-config/f (:lwjgl-app-config config))))
