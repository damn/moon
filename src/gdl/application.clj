(ns gdl.application
  (:require [clj.api.com.badlogic.gdx.application.listener :as listener]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application :as application]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application.config :as config]
            [clj.api.com.badlogic.gdx.gdx :as gdx]))

(defn start!
  [{:keys [title
           window
           fps
           create!
           dispose!
           render!
           resize!]}]
  (config/use-glfw-async!)
  (application/create (listener/create
                       {:create! (fn []
                                   (create! {:ctx/audio    (gdx/audio)
                                             :ctx/graphics (gdx/graphics)
                                             :ctx/files    (gdx/files)
                                             :ctx/input    (gdx/input)}))
                        :dispose! (fn []
                                    (dispose!))
                        :render! (fn []
                                   (render!))
                        :resize! (fn [width height]
                                   (resize! width height))
                        :pause! (fn [])
                        :resume! (fn [])})
                      (doto (config/create)
                        (config/set-title! title)
                        (config/set-windowed-mode! window)
                        (config/set-foreground-fps! fps))))
