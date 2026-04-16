(ns clojure.gdx.application-listener
  (:require [clj.api.com.badlogic.gdx.application.listener :as listener]))

(defn create [{:keys [state
                      create!
                      dispose!
                      render!
                      resize!]}]
  (listener/create
   (let [state @state]
     {:create! (fn []
                 ; just pass Gdx/app ??
                 (reset! state (let [[create-fn create-params] create!]
                                 (create-fn create-params))))
      :dispose! (fn []
                  (dispose! @state))
      :render! (fn []
                 (let [[render-fn render-params] render!]
                   (swap! state render-fn render-params)))
      :resize! (fn [width height]
                 (resize! @state width height))
      :pause! (fn [])
      :resume! (fn [])})))
