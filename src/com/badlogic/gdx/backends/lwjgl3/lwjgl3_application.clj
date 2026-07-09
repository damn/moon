(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [application-listener lwjgl3-application-configuration]
  (Lwjgl3Application. application-listener
                      lwjgl3-application-configuration))
