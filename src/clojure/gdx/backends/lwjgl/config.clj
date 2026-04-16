(ns clojure.gdx.backends.lwjgl.config
  (:require [clj.api.com.badlogic.gdx.backends.lwjgl3.application.config :as config]))

(def use-glfw-async! config/use-glfw-async!)

(defn create
  [{:keys [
           title
           window
           fps
           ]}]
  (doto (config/create)
    (config/set-title! title)
    (config/set-windowed-mode! window)
    (config/set-foreground-fps! fps)))
