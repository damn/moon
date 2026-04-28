(ns moon.start
  (:require [clojure.gdx.backends.lwjgl3 :as lwjgl]
            [moon.application :as application])
  (:gen-class))

(defn -main []
  (lwjgl/use-glfw-async!)
  (lwjgl/application! application/listener
                      {:title "Cyber Dungeon Quest"
                       :windowed-mode {:width 1440
                                       :height 900}
                       :foreground-fps 60}))
