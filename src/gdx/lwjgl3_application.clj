(ns gdx.lwjgl3-application
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [gdx.application-listener :as application-listener]
            [gdx.lwjgl3-application-configuration :as config]))

(defn create [opts]
  (lwjgl3-application/new
    (application-listener/create opts)
    (config/create opts)))
