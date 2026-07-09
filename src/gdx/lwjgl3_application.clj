(ns gdx.lwjgl3-application
  (:require [com.badlogic.gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]))

(defn create [application-listener
              lwjgl3-application-configuration]
  (lwjgl3-application/new application-listener
                          lwjgl3-application-configuration))
