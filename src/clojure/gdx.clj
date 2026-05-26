(ns clojure.gdx
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]

            clojure.gdx.application

            clojure.gdx.audio
            clojure.gdx.audio.sound

            clojure.gdx.files
            clojure.gdx.files.file-handle

            clojure.gdx.graphics
            clojure.gdx.graphics.color
            clojure.gdx.graphics.gl20
            clojure.gdx.graphics.texture
            clojure.gdx.graphics.g2d.bitmap-font
            clojure.gdx.graphics.g2d.bitmap-font.data
            clojure.gdx.graphics.g2d.texture-region

            clojure.gdx.graphics.g2d.freetype.font-generator

            clojure.gdx.input

            clojure.gdx.scenes.scene2d.actor
            clojure.gdx.scenes.scene2d.group
            clojure.gdx.scenes.scene2d.event

            clojure.gdx.scenes.scene2d.ui.widget
            clojure.gdx.scenes.scene2d.ui.scroll-pane
            clojure.gdx.scenes.scene2d.ui.horizontal-group
            clojure.gdx.scenes.scene2d.ui.label
            clojure.gdx.scenes.scene2d.ui.image-button
            clojure.gdx.scenes.scene2d.ui.select-box
            clojure.gdx.scenes.scene2d.ui.stack
            clojure.gdx.scenes.scene2d.ui.image
            clojure.gdx.scenes.scene2d.ui.text-button
            clojure.gdx.scenes.scene2d.ui.text-field
            clojure.gdx.scenes.scene2d.ui.text-tooltip
            clojure.gdx.scenes.scene2d.ui.skin
            clojure.gdx.scenes.scene2d.ui.table
            clojure.gdx.scenes.scene2d.ui.image
            clojure.gdx.scenes.scene2d.ui.check-box
            clojure.gdx.scenes.scene2d.ui.window
            clojure.gdx.scenes.scene2d.ui.widget-group

            clojure.gdx.scenes.scene2d.utils.texture-region-drawable
            clojure.gdx.scenes.scene2d.utils.change-listener
            clojure.gdx.scenes.scene2d.utils.click-listener

            clojure.gdx.maps.map-layers
            clojure.gdx.maps.map-properties
            clojure.gdx.maps.tiled.tiled-map
            clojure.gdx.maps.tiled.tiled-map-tile-layer
            clojure.gdx.maps.tiled.tiled-map-tile-layer.cell
            clojure.gdx.maps.tiled.tiles.static-tiled-map-tile
            clojure.gdx.maps.tiled.tmx-map-loader
            clojure.gdx.maps.renderer

            clojure.gdx.utils.disposable

            space.earlygrey.shape-drawer)
  (:import (com.badlogic.gdx ApplicationListener)
           (com.badlogic.gdx.backends.lwjgl3 Lwjgl3Application
                                             Lwjgl3ApplicationConfiguration)))

(defn application!
  [{:keys [title
           windowed-mode
           foreground-fps
           create!
           dispose!
           render!
           resize!
           pause!
           resume!]}]
  (Lwjgl3ApplicationConfiguration/useGlfwAsync)
  (Lwjgl3Application. (reify ApplicationListener
                        (create [_]
                          (create!))

                        (dispose [_]
                          (dispose!))

                        (render [_]
                          (render!))

                        (resize [_ width height]
                          (resize! width height))

                        (pause [_]
                          (pause!))

                        (resume [_]
                          (resume!)))
                      (doto (Lwjgl3ApplicationConfiguration.)
                        (.setTitle title)
                        (.setWindowedMode (:width windowed-mode) (:height windowed-mode))
                        (.setForegroundFPS foreground-fps))))
