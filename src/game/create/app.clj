(ns game.create.app
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.app :as app]
            [clojure.audio :as audio]
            [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.gdx.graphics.colors :as colors]
            [clojure.gdx.graphics.pixmap]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.graphics :as graphics]
            [clojure.graphics.batch :as batch]
            [clojure.graphics.texture :as texture]
            [clojure.graphics.pixmap :as pixmap]
            [clojure.graphics.texture.filter :as texture.filter]
            [clojure.graphics.g2d.bitmap-font :as bitmap-font]
            [clojure.graphics.g2d.bitmap-font.data :as font.data]
            [clojure.graphics.g2d.freetype.font-generator :as font-generator]
            [clojure.input :as input]
            [clojure.scene2d.ui.skin :as skin]))

(defn step [app]
  (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
  (tooltip-manager/set-initial-time! 0)
  (let [batch (sprite-batch/create)
        white-pixel-texture (let [pixmap (doto (clojure.gdx.graphics.pixmap/create 1 1)
                                           (pixmap/set-color! 1 1 1 1)
                                           (pixmap/draw-pixel! 0 0))
                                  texture (pixmap/texture pixmap)]
                              (pixmap/dispose! pixmap)
                              texture)
        world-unit-scale (float (/ 48))]
    {:ctx/app app
     :ctx/audio (into {}
                      (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                        [sound-name
                         (audio/new-sound (app/audio app)
                                          (files/internal (app/files app) (format "sounds/%s.wav" sound-name)))]))
     :ctx/batch batch
     :ctx/shape-drawer-texture white-pixel-texture
     :ctx/shape-drawer (batch/shape-drawer batch (texture/region white-pixel-texture 1 0 1 1))
     :ctx/default-font (let [path "exocet/films.EXL_____.ttf"
                             size 16
                             quality-scaling 2
                             generator (file-handle/freetype-font-generator (files/internal (app/files app) path))
                             font (font-generator/generate-font generator
                                                                {:size (* size quality-scaling)
                                                                 ; texture.filter/linear because scaling to world-units
                                                                 :min-filter texture.filter/linear
                                                                 :mag-filter texture.filter/linear})]
                         (font-generator/dispose! generator)
                         (font.data/set-scale! (bitmap-font/data font) (/ quality-scaling))
                         (font.data/set-markup-enabled! (bitmap-font/data font) true)
                         (bitmap-font/set-use-integer-positions! font false)
                         font)
     :ctx/world-unit-scale world-unit-scale
     :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                               world-height (* 900  world-unit-scale)]
                           (fit-viewport/create world-width
                                                world-height
                                                (app/orthographic-camera
                                                 {:y-down? false
                                                  :world-width world-width
                                                  :world-height world-height})))
     :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                    (update-vals data
                                 (fn [[path [hotspot-x hotspot-y]]]
                                   (let [pixmap (file-handle/pixmap (files/internal (app/files app) (format path-format path)))
                                         cursor (graphics/new-cursor (app/graphics app) pixmap hotspot-x hotspot-y)]
                                     (pixmap/dispose! pixmap)
                                     cursor))))
     :ctx/stage (let [stage (app/stage (fit-viewport/create 1440 900) batch)]
                  (input/set-processor! (app/input app) stage)
                  stage)
     :ctx/skin (let [skin (file-handle/skin (files/internal (app/files app) "uiskin.json"))]
                 (-> skin
                     (skin/font "default-font")
                     bitmap-font/data
                     (font.data/set-markup-enabled! true))
                 skin)
     :ctx/unit-scale (atom 1)}))
