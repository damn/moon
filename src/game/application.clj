(ns game.application
  (:require [application.create :as create]
            [application.dispose :as dispose]
            [application.resize :as resize]
            [application.render :as render]
            [gdx.application-listener :as application-listener]
            [gdx.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [gdx.backends.lwjgl3.lwjgl3-application-configuration :as lwjgl3-application-configuration])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (lwjgl3-application-configuration/use-glfw-async!)
  (lwjgl3-application/create (application-listener/create
                              {:create! (fn [application]
                                          (reset! state (create/do! application)))
                               :dispose! (fn []
                                           (dispose/do! @state))
                               :render! (fn []
                                          (swap! state render/do!))
                               :resize! (fn [width height]
                                          (resize/do! @state width height))
                               :pause! (fn [])
                               :resume! (fn [])})
                             (lwjgl3-application-configuration/create
                              {:title "Moon"
                               :windowed-mode {:width 1440 :height 900}
                               :foreground-fps 60})))
