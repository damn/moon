(ns com.badlogic.gdx.backends.lwjgl3.lwjgl3-application
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn new [listener config]
  (Lwjgl3Application. listener config))
