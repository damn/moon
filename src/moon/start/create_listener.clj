(ns moon.start.create-listener
  (:require [clj.api.com.badlogic.gdx.application.listener :as listener]))

(defn step
  [ctx
   {:keys [state
           create!
           dispose!
           render!
           resize!]}]
  (assoc ctx :app/listener
         (let [state @state
               [create-fn create-params] create!
               [render-fn render-params] render!]
           (listener/create
            {:create! (fn []
                        (reset! state (create-fn create-params)))
             :dispose! (fn []
                         (dispose! @state))
             :render! (fn []
                        (swap! state render-fn render-params))
             :resize! (fn [width height]
                        (resize! @state width height))
             :pause! (fn [])
             :resume! (fn [])}))))
