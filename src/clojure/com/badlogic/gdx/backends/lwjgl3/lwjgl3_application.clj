(ns clojure.com.badlogic.gdx.backends.lwjgl3.lwjgl3-application
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(defn create [^ApplicationListener listener ^Lwjgl3ApplicationConfiguration config]
  (Lwjgl3Application. listener config))
