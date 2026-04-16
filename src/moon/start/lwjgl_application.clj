(ns moon.start.lwjgl-application
  (:require [clj.api.com.badlogic.gdx.application.listener :as listener]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application :as application]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application.config :as config]
            [clj.api.com.badlogic.gdx.gdx :as gdx]))

(defn step
  [{:keys [title
           window
           fps
           state
           create!
           dispose!
           render!
           resize!]}]
  (let [state @state
        [create-fn create-params] create!
        [render-fn render-params] render!]
    (config/use-glfw-async!)
    (application/create (listener/create
                         {:create! (fn []
                                     (reset! state
                                             (create-fn {:ctx/audio    (gdx/audio)
                                                         :ctx/graphics (gdx/graphics)
                                                         :ctx/files    (gdx/files)
                                                         :ctx/input    (gdx/input)}
                                                        create-params)))
                          :dispose! (fn []
                                      (dispose! @state))
                          :render! (fn []
                                     (swap! state render-fn render-params))
                          :resize! (fn [width height]
                                     (resize! @state width height))
                          :pause! (fn [])
                          :resume! (fn [])})
                        (doto (config/create)
                          (config/set-title! title)
                          (config/set-windowed-mode! window)
                          (config/set-foreground-fps! fps)))))
