(ns editor.app
  (:require [clojure.string :as str]
            [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.scenes.scene2d.event :as event]
            [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]
            [gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.utils.screen-utils :as screen-utils]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            schema.malli-form
            editor.widget.animation
            editor.widget.boolean
            editor.widget.enum
            editor.widget.image
            editor.widget.map
            editor.widget.number
            editor.widget.one-to-many
            editor.widget.one-to-one
            editor.widget.sound
            editor.widget.string
            editor.widget.val-max
            [gdx.backends.lwjgl :as lwjgl]
            [gdx.scenes.scene2d.ui.text-button :as text-button]
            [gdx.scenes.scene2d.ui.window :as window]
            [gdx.stage :as stage]
            gdx.textures
            [gdx.viewport :as viewport]
            [editor.main-window :as main-window]
            [moon.db :as db]))

(defn create! [app _params]
  (let [skin (skin/create (files/internal (app/files app) "skin/uiskin.json"))
        ui-viewport (fit-viewport/create 1440 900)
        batch (sprite-batch/create)
        stage (stage/create ui-viewport batch)
        db (db/create)]
    (input/set-processor! (app/input app) stage)
    (stage/add-actor! stage (main-window/create db skin))
    {:ctx/app app
     :ctx/batch batch
     :ctx/db db
     :ctx/skin skin
     :ctx/stage stage
     :ctx/textures (gdx.textures/create (app/files app))
     :ctx/ui-viewport ui-viewport}))

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
    :create! create!
    :create-params nil
    :dispose! dispose!
    :render! render!
    :render-params nil
    :resize! resize!
    :title "!Editor!"
    :windowed-mode {:width 1440 :height 900}
    :foreground-fps 60}))
