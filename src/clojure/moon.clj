(ns clojure.moon
  (:require [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.moon.create :as create]
            [clojure.moon.dispose :as dispose]
            [clojure.moon.render :as render]
            [clojure.moon.resize :as resize])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (lwjgl3-application/f
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
   {:title "Moon"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))
