(ns moon.start
  (:require [clojure.gdx.backends.lwjgl.application :as lwjgl-app]
            [clojure.gdx.backends.lwjgl.application.config :as config]
            [moon.application :as application])
  (:gen-class))

(defn -main []
  (config/use-glfw-async!)
  (lwjgl-app/create application/listener
                    (config/create
                     {:title "Moon"
                      :windowed-mode {:width 1440
                                      :height 900}
                      :foreground-fps 60})))
