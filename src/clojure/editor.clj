(ns clojure.editor
  (:require [clojure.application :as application]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.editor.create :as create]
            [clojure.editor.dispose :as dispose]
            [clojure.editor.render :as render]
            [clojure.editor.resize :as resize]))

(defn -main []
  (lwjgl3-application/f
   {:create! (fn [app]
               (reset! application/state (create/create app)))
    :dispose! (fn []
                (dispose/dispose @application/state))
    :render! (fn []
               (swap! application/state render/render))
    :resize! (fn [width height]
               (resize/resize @application/state width height))
    :pause! (fn [])
    :resume! (fn [])}
   {:title "!Editor!"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))
