(ns clojure.gdx.backends.lwjgl
  (:require [clj.api.com.badlogic.gdx.application.listener :as listener]
            [clj.api.com.badlogic.gdx.backends.lwjgl3.application :as application]
            [clojure.gdx.backends.lwjgl.config :as config]))

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
                      (config/create config)))
