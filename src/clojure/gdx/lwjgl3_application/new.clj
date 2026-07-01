(ns clojure.gdx.lwjgl3-application.new
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn f [listener config]
  (Lwjgl3Application. listener config))
