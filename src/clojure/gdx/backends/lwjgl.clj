(ns clojure.gdx.backends.lwjgl
  (:require [clj.api.com.badlogic.gdx.application.listener :as listener]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application :as application]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application.config :as config]))

(def use-glfw-async! config/use-glfw-async!)

(defn application! [listener {:keys [title window fps]}]
  (application/create (listener/create listener)
                      (doto (config/create)
                        (config/set-title! title)
                        (config/set-windowed-mode! window)
                        (config/set-foreground-fps! fps))))
