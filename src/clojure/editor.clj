(ns clojure.editor
  (:require [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.editor.create :as create]
            [clojure.editor.dispose :as dispose]
            [clojure.editor.render :as render]
            [clojure.editor.resize :as resize]))

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
   {:title "!Editor!"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))
