(ns clojure.gdx.backends.lwjgl3.lwjgl3-application
  (:require [clojure.gdx.application-listener :as application-listener]
            [clojure.gdx.backends.lwjgl3.lwjgl3-application-configuration :as config]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(defn create
  [{:keys [application/config
           application/listener]}]
  (lwjgl3-application/new (application-listener/create listener)
                          (config/create config)))
