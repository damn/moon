(ns gdx.lwjgl3-application
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [gdx.application-listener :as application-listener]))

(defn create [listener-spec configuration]
  (let [listener (application-listener/create listener-spec)]
    (lwjgl3-application/new listener
                            configuration)))
