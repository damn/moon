(ns clojure.gdx.backends.lwjgl.application
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [listener config]
  (Lwjgl3Application. listener config))
