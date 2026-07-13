(ns gdx.backends.lwjgl3.lwjgl3-application
  (:require [gdx.application-listener :as application-listener]
            [gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(defn create
  [{:keys [application/config
           application/listener]}]
  (lwjgl3-application/new (application-listener/create listener)
                          (config/create config)))
