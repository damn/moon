(ns moon.start.lwjgl-application
  (:require [gdl.application :as application]))

(defn step
  [_ctx
   {:keys [state
           create!
           dispose!
           render!
           resize!]
    :as config}]
  (application/start!
   (let [state @state
         [create-fn create-params] create!
         [render-fn render-params] render!]
     (merge config
            {:create! (fn [ctx]
                        (reset! state (create-fn ctx create-params)))
             :dispose! (fn []
                         (dispose! @state))
             :render! (fn []
                        (swap! state render-fn render-params))
             :resize! (fn [width height]
                        (resize! @state width height))}))))
