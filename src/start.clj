(ns start
  (:require [game.application :as app]
            [gdx.backends.lwjgl :as lwjgl])
  (:gen-class))

(defn -main []
  (lwjgl/application!
   {:create!  app/create!
    :dispose! app/dispose!
    :render!  app/render!
    :resize!  app/resize!
    :pause!   app/pause!
    :resume!  app/resume!
    :title "Moon"
    :windowed-mode {:width 1440
                    :height 900}
    :foreground-fps 60}))
