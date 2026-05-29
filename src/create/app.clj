(ns create.app
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gdx.application :as app]
            [gdx.files :as files]
            [gdx.graphics :as graphics]
            [gdx.graphics.colors :as colors]
            [gdx.graphics.orthographic-camera :as camera]
            [gdx.graphics.shape-drawer :as shape-drawer]
            [gdx.input :as input]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [gdx.utils.viewport.fit-viewport :as fit-viewport])
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn step [app]
  (tooltip-manager/set-initial-time! 0)
  (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  (let [batch (com.badlogic.gdx.graphics.g2d.SpriteBatch.)
        white-pixel-texture (graphics/white-pixel-texture)
        world-unit-scale (float (/ 48))]
    {:ctx/app app
     :ctx/audio (into {}
                      (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                        [sound-name
                         (.newSound (app/audio app)
                                    (files/internal (app/files app) (format "sounds/%s.wav" sound-name)))]))
     :ctx/batch batch
     :ctx/shape-drawer-texture white-pixel-texture
     :ctx/shape-drawer (shape-drawer/create batch (com.badlogic.gdx.graphics.g2d.TextureRegion. white-pixel-texture 1 0 1 1))
     :ctx/default-font (let [path "exocet/films.EXL_____.ttf"
                             size 16
                             quality-scaling 2
                             generator (FreeTypeFontGenerator. (files/internal (app/files app) path))
                             font (.generateFont generator
                                                 (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                                                   (set! (.size params) (* size quality-scaling))
                                                   ; texture.filter/linear because scaling to world-units
                                                   (set! (.minFilter params) Texture$TextureFilter/Linear)
                                                   (set! (.magFilter params) Texture$TextureFilter/Linear)
                                                   params))]
                         (.dispose generator)
                         (.setScale (.getData font) (/ quality-scaling))
                         (set! (.markupEnabled (.getData font)) true)
                         (.setUseIntegerPositions font false)
                         font)
     :ctx/world-unit-scale world-unit-scale
     :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                               world-height (* 900  world-unit-scale)]
                           (fit-viewport/create world-width
                                                world-height
                                                (camera/create
                                                 {:y-down? false
                                                  :world-width world-width
                                                  :world-height world-height})))
     :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                    (update-vals data
                                 (fn [[path [hotspot-x hotspot-y]]]
                                   (let [pixmap (Pixmap. (files/internal (app/files app) (format path-format path)))
                                         cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
                                     (.dispose pixmap)
                                     cursor))))
     :ctx/stage (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
                  (input/set-processor! (app/input app) stage)
                  stage)
     :ctx/skin (let [skin (Skin. (files/internal (app/files app) "uiskin.json"))]
                 (set! (.markupEnabled (-> skin
                                           (.getFont "default-font")
                                           .getData))
                       true)
                 skin)
     :ctx/unit-scale (atom 1)}))
