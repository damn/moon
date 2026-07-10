(ns clojure.editor
  (:require [gdl.backends.lwjgl3.lwjgl3-application :as lwjgl3-application]
            [clojure.editor.create :as create]
            [clojure.editor.dispose :as dispose]
            [clojure.editor.render :as render]
            [clojure.editor.resize :as resize]))

(def state (atom nil))

(defn -main []
  (lwjgl3-application/create {:create! (fn [app]
                                         (reset! state (create/create app)))
                              :dispose! (fn []
                                          (dispose/dispose @state))
                              :render! (fn []
                                         (swap! state render/render))
                              :resize! (fn [width height]
                                         (resize/resize @state width height))}
                             {:config/set-title "!Editor!"
                              :config/set-windowed-mode {:width 1440
                                                         :height 900}
                              :config/set-foreground-fps 60}))
