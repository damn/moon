(ns levelgen-test.application
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration]
            levelgen-test.application-listener
            levelgen-test.state))

(defn start!
  [config]
  (lwjgl3-application/new (application-listener/new
                         (levelgen-test.application-listener/f levelgen-test.state/application
                                                               config))
                        (lwjgl3-application-configuration/new (:lwjgl-app-config config))))
