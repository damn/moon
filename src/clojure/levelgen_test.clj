(ns clojure.levelgen-test
  (:require [gdl.lwjgl3-application :as lwjgl3-application]
            [clojure.levelgen-test.create :as create]
            [clojure.levelgen-test.dispose :as dispose]
            [clojure.levelgen-test.render :as render]
            [clojure.levelgen-test.resize :as resize]
            [clojure.levels.modules :as modules]
            [clojure.levels.tmx :as tmx]
            [clojure.levels.uf-caves :as uf-caves]))

(def state (atom nil))

(def ^:private config
  {:initial-level-fn uf-caves/create
   :level-fns [["Vampire" tmx/vampire]
               ["UF Caves" uf-caves/create]
               ["Modules" modules/create]]
   :ui-viewport-width 1440
   :ui-viewport-height 900
   :world-viewport-width 1440
   :world-viewport-height 900
   :tile-size 48
   :ui-skin-path "skin/uiskin.json"
   :textures-config {:folder "resources/"
                     :extensions #{"png" "bmp"}}
   :zoom-speed 0.1
   :camera-movement-speed 1})

(defn -main []
  (lwjgl3-application/create
   {:create! (fn [app]
               (reset! state (create/create state config app)))
    :dispose! (fn []
                (dispose/dispose @state))
    :render! (fn []
               (swap! state render/render))
    :resize! (fn [width height]
               (resize/resize @state width height))
    :pause! (fn [])
    :resume! (fn [])}
   {:config/set-title "Levelgen Test"
    :config/set-windowed-mode {:width 1440
                               :height 900}
    :config/set-foreground-fps 60}))
