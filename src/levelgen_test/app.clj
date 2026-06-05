(ns levelgen-test.app
  (:require [clojure.use-glfw-async :as use-glfw-async]
            [clojure.gdx.backends.lwjgl.application :as application]
            [clojure.gdx.utils.disposable :as disposable]
            [clojure.gdx.utils.viewport :as viewport]
            [levelgen-test.create :as create]
            [levelgen-test.render :as render]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/sprite-batch
           ctx/tiled-map]}]
  ; TODO TEXTURES NOT DISPOSED
  (disposable/dispose! skin)
  (disposable/dispose! sprite-batch)
  (disposable/dispose! tiled-map))

(defn resize!
  [{:keys [ctx/stage
           ctx/world-viewport]}
   width height]
  (viewport/update! (:stage/viewport stage) width height true)
  (viewport/update! world-viewport width height false))

(def state (atom nil))

(defn -main []
  (use-glfw-async/f!)
  (application/start!
   {:state-var #'state
    :create-pipeline [[create/f!]]
    :dispose! dispose!
    :render-pipeline [[render/f!]]
    :resize! resize!
    :title "Levelgen Test"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))
