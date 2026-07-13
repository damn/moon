(ns gdx.application.lwjgl
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [gdx.application-listener :as application-listener]
            [gdx.config :as app-config]))

(defn create
  [{:keys [config listener]}]
  (lwjgl3-application/new (application-listener/create listener)
                          (app-config/create config)))
