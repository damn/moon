(ns gdx.lwjgl3-application
  (:require [gdx.application-listener :as application-listener]
            [gdx.lwjgl3-application-configuration :as config])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn create [opts]
  (Lwjgl3Application. (application-listener/create opts)
                      (config/create opts)))
