(ns moon.start.lwjgl-application
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn step
  [{:keys [app/config
           app/listener]}]
  (Lwjgl3Application. listener config))
