(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn new [application-listener lwjgl3-application-configuration]
  (Lwjgl3Application. application-listener
                      lwjgl3-application-configuration))
