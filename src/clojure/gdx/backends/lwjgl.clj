(ns clojure.gdx.backends.lwjgl
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn application!
  [listener config]
  (Lwjgl3Application. listener config))
