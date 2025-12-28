(ns clojure.gdx.backends.lwjgl3.application
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [application-listener application-configuration]
  (Lwjgl3Application. application-listener
                      application-configuration))
