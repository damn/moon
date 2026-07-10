(ns clojure.moon
  (:require [gdl.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [clojure.moon.create :as create]
            [clojure.moon.dispose :as dispose]
            [clojure.moon.render :as render]
            [clojure.moon.resize :as resize])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (lwjgl3-application/create
   {:create! (fn [app]
               (reset! state (create/create app)))
    :dispose! (fn []
                (dispose/dispose @state))
    :render! (fn []
               (swap! state render/render))
    :resize! (fn [width height]
               (resize/resize @state width height))
    :pause! (fn [])
    :resume! (fn [])}
   {:config/set-title "Moon"
    :config/set-windowed-mode {:width 1440
                               :height 900}
    :config/set-foreground-fps 60}))
