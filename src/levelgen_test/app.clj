(ns levelgen-test.app
  (:require [gdx.backends.lwjgl :as lwjgl]
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
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport    width height true)
  (viewport/update! world-viewport width height false))

(def state (atom nil))

(defn -main []
  (lwjgl/use-glfw-async!)
  (lwjgl/application!
   {:state-var #'state
    :create! create/f!
    :create-params nil
    :dispose! dispose!
    :render! render/f!
    :render-params nil
    :resize! resize!
    :title "Levelgen Test"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))
