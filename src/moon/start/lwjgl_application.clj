(ns moon.start.lwjgl-application
  (:require [clojure.gdx.backends.lwjgl :as lwjgl]))

(defn step
  [{:keys [config
           state
           create!
           dispose!
           render!
           resize!
           colors]}]
  (lwjgl/application! (let [state @state
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
                         :resume! (fn [])})
                      config))
