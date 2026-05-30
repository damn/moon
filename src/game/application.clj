(ns game.application
  (:require [application.create :as create]
            [application.dispose :as dispose]
            [application.resize :as resize]
            [application.render :as render]))

(def state (atom nil))

(defn create! []
  (reset! state (create/do!)))

(defn dispose! []
  (dispose/do! @state))

(defn render! []
  (swap! state render/do!))

(defn resize! [width height]
  (resize/do! @state width height))

(defn pause! [])

(defn resume! [])
