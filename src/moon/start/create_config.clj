(ns moon.start.create-config
  (:require [clj.api.com.badlogic.gdx.backends.lwjgl3.application.config :as config]))

(defn step [ctx {:keys [title window fps]}]
  (assoc ctx :app/config
         (doto (config/create)
           (config/set-title! title)
           (config/set-windowed-mode! window)
           (config/set-foreground-fps! fps))))
