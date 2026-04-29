(ns moon.application.create.gdx
  (:require [clojure.edn :as edn]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.graphics :as graphics]
            [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.freetype :as freetype]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [clojure.graphics.texture :as texture]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]
            [clojure.input :as input]
            [clojure.java.io :as io]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.graphics Color
                                      Colors
                                      Pixmap
                                      Pixmap$Format
                                      Texture)
           (com.badlogic.gdx.graphics.g2d SpriteBatch)
           (com.badlogic.gdx.scenes.scene2d.ui TooltipManager)))

(defn step [^Application app]
  (doseq [[name [r g b a]] {"PRETTY_NAME" [0.84 0.8 0.52 1]}]
    (Colors/put name (Color. r g b a)))
  (set! (.initialTime (TooltipManager/getInstance)) 0)
  (let [batch (SpriteBatch.)
        white-pixel-texture (let [pixmap (doto (Pixmap. 1 1 Pixmap$Format/RGBA8888)
                                           (.setColor 1 1 1 1)
                                           (.drawPixel 0 0))
                                  texture (Texture. pixmap)]
                              (.dispose pixmap)
                              texture)
        ui-viewport (viewport/create 1440 900 (camera/create))
        world-unit-scale (float (/ 48))]
    {:ctx/audio (into {}
                      (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                        [sound-name
                         (.newSound (.getAudio app) (.internal (.getFiles app) (format "sounds/%s.wav" sound-name)))]))
     :ctx/batch batch
     :ctx/files     (.getFiles app)
     :ctx/graphics  (.getGraphics app)
     :ctx/input     (.getInput app)
     :ctx/shape-drawer (shape-drawer/create batch (texture/region white-pixel-texture 1 0 1 1))
     :ctx/shape-drawer-texture white-pixel-texture
     :ctx/ui-viewport ui-viewport
     :ctx/stage (let [stage (stage/create ui-viewport batch)]
                  (input/set-processor! (.getInput app) stage)
                  stage)
     :ctx/skin (let [skin (skin/create (.internal (.getFiles app) "uiskin.json"))]
                 (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                 skin)
     :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                    (update-vals data
                                 (fn [[path [hotspot-x hotspot-y]]]
                                   (graphics/new-cursor (.getGraphics app)
                                                        (.internal (.getFiles app) (format path-format path))
                                                        hotspot-x
                                                        hotspot-y))))
     :ctx/world-unit-scale world-unit-scale
     :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                               world-height (* 900  world-unit-scale)]
                           (viewport/create world-width
                                            world-height
                                            (doto (camera/create)
                                              (camera/set-to-ortho! false world-width world-height))))
     :ctx/textures (textures/create (.getFiles app)
                                    {:folder "resources/"
                                     :extensions #{"png" "bmp"}})
     :ctx/default-font (let [{:keys [path
                                     size
                                     quality-scaling
                                     enable-markup?
                                     use-integer-positions?]} {
                                                               :path "exocet/films.EXL_____.ttf"
                                                               :size 16
                                                               :quality-scaling 2
                                                               :enable-markup? true
                                                               :use-integer-positions? false
                                                               ; :texture-filter/linear because scaling to world-units
                                                               :min-filter :linear
                                                               :mag-filter :linear
                                                               }]
                         (doto (freetype/generate-font (.internal (.getFiles app) path)
                                                       {:size (* size quality-scaling)})
                           (bitmap-font/set-scale! (/ quality-scaling))
                           (bitmap-font/enable-markup! enable-markup?)
                           (bitmap-font/use-integer-positions! use-integer-positions?)))}))
