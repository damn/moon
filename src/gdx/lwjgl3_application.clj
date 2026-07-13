(ns gdx.lwjgl3-application
  (:require [com.badlogic.gdx.application-listener :as application-listener]
            [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(defn create [listener-spec configuration]
  (let [listener (application-listener/new listener-spec)]
    (lwjgl3-application/new listener configuration)))
