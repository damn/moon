(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [listener config]
  (Lwjgl3Application. listener config))
