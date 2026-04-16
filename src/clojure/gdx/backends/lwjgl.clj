(ns clojure.gdx.backends.lwjgl
  (:require [clj.api.com.badlogic.gdx.application.listener :as listener]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application :as application]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application.config :as config]))

(def use-glfw-async! config/use-glfw-async!)

(defn application!
  [
   {:keys [
           config
           state
           create!
           dispose!
           render!
           resize!
           ]}
   ]
  (application/create (listener/create
                       (let [state @state
                             [create-fn create-params] create!
                             [render-fn render-params] render!]
                         {:create! (fn []
                                     (reset! state (create-fn create-params)))
                          :dispose! (fn []
                                      (dispose! @state))
                          :render! (fn []
                                     (swap! state render-fn render-params))
                          :resize! (fn [width height]
                                     (resize! @state width height))
                          :pause! (fn [])
                          :resume! (fn [])}))
                      (let [{:keys [
                                    title
                                    window
                                    fps
                                    ]} config]
                        (doto (config/create)
                          (config/set-title! title)
                          (config/set-windowed-mode! window)
                          (config/set-foreground-fps! fps)))))
