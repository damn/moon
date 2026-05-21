(ns clojure.gdx.backends.lwjgl3.application
  (:require [clojure.gdx.application-listener :as listener]
            [clojure.gdx.backends.lwjgl3.application-configuration :as config])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [listener config]
  (Lwjgl3Application. (listener/create listener)
                      (config/create config)))
