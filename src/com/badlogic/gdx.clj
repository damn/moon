(ns com.badlogic.gdx
  (:require com.badlogic.gdx.application

            com.badlogic.gdx.audio
            com.badlogic.gdx.audio.sound

            com.badlogic.gdx.files
            com.badlogic.gdx.files.file-handle

            com.badlogic.gdx.graphics
            [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            com.badlogic.gdx.graphics.texture
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            com.badlogic.gdx.graphics.g2d.bitmap-font
            com.badlogic.gdx.graphics.g2d.bitmap-font.data
            com.badlogic.gdx.graphics.g2d.texture-region
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]

            com.badlogic.gdx.graphics.g2d.freetype.font-generator

            com.badlogic.gdx.input

            [com.badlogic.gdx.backends.lwjgl3.application :as application]

            [com.badlogic.gdx.scenes.scene2d.ctx-stage :as ctx-stage]
            com.badlogic.gdx.scenes.scene2d.actor
            com.badlogic.gdx.scenes.scene2d.group
            com.badlogic.gdx.scenes.scene2d.event

            com.badlogic.gdx.scenes.scene2d.ui.widget
            com.badlogic.gdx.scenes.scene2d.ui.scroll-pane
            com.badlogic.gdx.scenes.scene2d.ui.horizontal-group
            com.badlogic.gdx.scenes.scene2d.ui.label
            com.badlogic.gdx.scenes.scene2d.ui.image-button
            com.badlogic.gdx.scenes.scene2d.ui.select-box
            com.badlogic.gdx.scenes.scene2d.ui.stack
            com.badlogic.gdx.scenes.scene2d.ui.image
            com.badlogic.gdx.scenes.scene2d.ui.text-button
            com.badlogic.gdx.scenes.scene2d.ui.text-field
            com.badlogic.gdx.scenes.scene2d.ui.skin
            com.badlogic.gdx.scenes.scene2d.ui.table
            com.badlogic.gdx.scenes.scene2d.ui.image
            com.badlogic.gdx.scenes.scene2d.ui.check-box
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            com.badlogic.gdx.scenes.scene2d.ui.window
            com.badlogic.gdx.scenes.scene2d.ui.widget-group

            com.badlogic.gdx.utils.disposable
            [com.badlogic.gdx.utils.screen-utils :as screen-utils]

            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            ))

(def clear-screen! screen-utils/clear!)

(def fit-viewport fit-viewport/create)

(def tooltip-manager-set-initial-time! tooltip-manager/set-initial-time!)

(def put-colors! colors/put!)

(def pixmap pixmap/create)

(def orthographic-camera orthographic-camera/create)

(def sprite-batch sprite-batch/create)

(def stage ctx-stage/create)

(def application! application/create)
