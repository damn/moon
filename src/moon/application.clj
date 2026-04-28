(ns moon.application
  (:require [moon.application.create :as create]
            [moon.application.dispose :as dispose]
            [moon.application.render :as render]
            [moon.application.resize :as resize]))

(def state (atom nil))

(def listener
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
