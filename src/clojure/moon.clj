(ns clojure.moon
  (:require [org.lwjgl.system.configuration :as configuration]
            [clojure.gdx :as gdx]
            [gdx.lwjgl3-application :as lwjgl3-application]
            [clojure.os :as os]
            [clojure.shared-library-loader :as shared-library-loader]
            [clojure.moon.create :as create]
            [clojure.moon.dispose :as dispose]
            [clojure.moon.render :as render]
            [clojure.moon.resize :as resize])
  (:gen-class))

(def state (atom nil))

(defn -main []
  (when (= shared-library-loader/os os/mac-os-x)
    (configuration/set! configuration/glfw-library-name "glfw_async"))
  (lwjgl3-application/create
   {:create! (fn []
               (reset! state (create/create (gdx/app))))
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
