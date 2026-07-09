(ns clojure.moon
  (:require [clojure.application-listener :as application-listener]
            [clojure.configuration :as configuration]
            [clojure.gdx :as gdx]
            [clojure.lwjgl3-application :as lwjgl3-application]
            [clojure.lwjgl3-application-configuration :as config]
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
   (application-listener/create
    {:create! (fn []
                (reset! state (create/create (gdx/app))))
     :dispose! (fn []
                 (dispose/dispose @state))
     :render! (fn []
                (swap! state render/render))
     :resize! (fn [width height]
                (resize/resize @state width height))
     :pause! (fn [])
     :resume! (fn [])})
   (config/build
    {:title "Moon"
     :windowed-mode {:width 1440 :height 900}
     :foreground-fps 60})))
