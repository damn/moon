(ns clojure.lwjgl3-application
  (:require [clojure.application-listener :as application-listener]
            [clojure.lwjgl3-application-configuration :as lwjgl3-config]
            [clojure.use-glfw-async :as use-glfw-async])
  (:import (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application)))

(defn f [listener config]
  (Lwjgl3Application. listener config))

(defn f!
  [{:keys [title windowed-mode foreground-fps]} listener-callbacks]
  (use-glfw-async/f)
  (f (application-listener/new listener-callbacks)
     (doto (lwjgl3-config/new)
       (lwjgl3-config/set-title! title)
       (lwjgl3-config/set-windowed-mode! (:width windowed-mode) (:height windowed-mode))
       (lwjgl3-config/set-foreground-fps! foreground-fps))))
