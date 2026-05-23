(ns clojure.gdx.backends.lwjgl3.application
  (:require [clojure.gdx.application-listener :as listener])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [listener config]
  (Lwjgl3Application. (listener/create listener)
                      config))
