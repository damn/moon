(ns editor.app
  (:require [com.badlogic.gdx.utils.screen-utils :as screen-utils]
            [editor.app.create :as create]
            [editor.widget.animation]
            [editor.widget.boolean]
            [editor.widget.enum]
            [editor.widget.image]
            [editor.widget.map]
            [editor.widget.number]
            [editor.widget.one-to-many]
            [editor.widget.one-to-one]
            [editor.widget.sound]
            [editor.widget.string]
            [editor.widget.val-max]
            [schema.malli-form]
            [gdx.backends.lwjgl :as lwjgl]
            [gdx.stage :as stage]
            [gdx.utils.disposable :as disposable]
            [gdx.viewport :as viewport]))

(defn dispose!
  [{:keys [ctx/skin
           ctx/batch]}]
  ; TODO textures not disposede
  (disposable/dispose! skin)
  (disposable/dispose! batch))

(defn render!
  [{:keys [ctx/stage]
    :as ctx}
   _params]
  (let [ctx (if-let [new-ctx (:stage/ctx stage)]
              new-ctx
              ctx)]
    (screen-utils/clear! 0 0 0 0)
    (stage/set-ctx! stage ctx)
    (stage/act! stage)
    (stage/draw! stage)
    (:stage/ctx stage)))

(defn resize!
  [{:keys [ctx/ui-viewport]} width height]
  (viewport/update! ui-viewport width height true))

(def state (atom nil))

(defn -main []
  (lwjgl/use-glfw-async!)
  (lwjgl/application!
   {:state-var #'state
    :create! create/f!
    :create-params nil
    :dispose! dispose!
    :render! render!
    :render-params nil
    :resize! resize!
    :title "!Editor!"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))
