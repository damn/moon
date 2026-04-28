(ns moon.application
  (:require [moon.application.create :as create]
            [moon.application.dispose :as dispose]
            [moon.application.render :as render]
            [moon.application.resize :as resize]))

(def state (atom nil))

(defn create! []
  (reset! state (create/do!)))

(defn dispose! []
  (dispose/do! @state))

(defn render! []
  (swap! state render/do!))

(defn resize! [width height]
  (resize/do! @state width height))
