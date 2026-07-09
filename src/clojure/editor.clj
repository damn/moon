(ns clojure.editor
  (:require [clojure.application-listener :as application-listener]
            [org.lwjgl.system.configuration :as configuration]
            [clojure.gdx :as gdx]
            [gdx.lwjgl3-application :as lwjgl3-application]
            [clojure.os :as os]
            [clojure.shared-library-loader :as shared-library-loader]
            [clojure.editor.create :as create]
            [clojure.editor.dispose :as dispose]
            [clojure.editor.render :as render]
            [clojure.editor.resize :as resize]))

(def state (atom nil))

(defn -main []
  (when (= shared-library-loader/os os/mac-os-x)
    (configuration/set! configuration/glfw-library-name "glfw_async"))
  (lwjgl3-application/create (application-listener/create
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
                             {:config/set-title "!Editor!"
                              :config/set-windowed-mode {:width 1440
                                                         :height 900}
                              :config/set-foreground-fps 60}))
