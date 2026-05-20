(ns com.badlogic.gdx.game
  (:require [clojure.config :refer [edn-resource]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.badlogic.gdx :as gdx]
            [com.badlogic.gdx.graphics]
            [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [com.badlogic.gdx.textures]
            [com.badlogic.gdx.scenes.scene2d.ctx-stage :as ctx-stage]
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [gdl.app :as app]
            [gdl.application-listener :as listener]
            [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.graphics :as graphics]
            [gdl.graphics.batch :as batch]
            [gdl.graphics.texture :as texture]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.data :as font.data]
            [gdl.input :as input]
            [gdl.scene2d.ui.skin :as skin])
  (:import (com.badlogic.gdx.graphics Pixmap
                                      Texture$TextureFilter)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator
                                                   FreeTypeFontGenerator$FreeTypeFontParameter)
           (com.badlogic.gdx.scenes.scene2d.ui Skin))
  (:gen-class))

(def state (atom nil))

(defn -main []
  (let [{:keys [create
                dispose
                render
                resize
                colors
                ]
         :as config
         } (edn-resource "start.edn")]
    (gdx/application! (reify listener/ApplicationListener
                        (create! [_ app]
                          (colors/put! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
                          (tooltip-manager/set-initial-time! 0)
                          (reset! state
                                  (reduce (fn [ctx [f & params]]
                                            (apply f ctx params))
                                          (let [batch (sprite-batch/create)
                                                white-pixel-texture (com.badlogic.gdx.graphics/white-pixel-texture)
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
                                                                     generator (FreeTypeFontGenerator. (files/internal (app/files app) path))
                                                                     font (.generateFont generator (let [params (FreeTypeFontGenerator$FreeTypeFontParameter.)]
                                                                                                     (set! (.size params) (* size quality-scaling))
                                                                                                     ; Texture$TextureFilter/Linear because scaling to world-units
                                                                                                     (set! (.minFilter params) Texture$TextureFilter/Linear)
                                                                                                     (set! (.magFilter params) Texture$TextureFilter/Linear)
                                                                                                     params))]
                                                                 (.dispose generator)
                                                                 (font.data/set-scale! (.getData font) (/ quality-scaling))
                                                                 (font.data/set-markup-enabled! (.getData font) true)
                                                                 (.setUseIntegerPositions font false)
                                                                 font)
                                             :ctx/world-unit-scale world-unit-scale
                                             :ctx/world-viewport (let [world-width  (* 1440 world-unit-scale)
                                                                       world-height (* 900  world-unit-scale)]
                                                                   (fit-viewport/create world-width
                                                                                        world-height
                                                                                        (orthographic-camera/create
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
                                             :ctx/stage (let [stage (ctx-stage/create (fit-viewport/create 1440 900) batch)]
                                                          (input/set-processor! (app/input app) stage)
                                                          stage)
                                             :ctx/skin (let [skin (Skin. (files/internal (app/files app) "uiskin.json"))]
                                                         (-> skin
                                                             (skin/font "default-font")
                                                             bitmap-font/data
                                                             (font.data/set-markup-enabled! true))
                                                         skin)
                                             :ctx/unit-scale (atom 1)
                                             :ctx/textures (com.badlogic.gdx.textures/create)})
                                          create)))

                        (dispose! [_]
                          (doseq [f dispose]
                            (f @state)))

                        (render! [_]
                          (swap! state
                                 (fn [ctx]
                                   (reduce (fn [ctx [f & params]]
                                             (apply f ctx params))
                                           ctx
                                           render))))

                        (resize! [_ width height]
                          (doseq [f resize]
                            (f @state width height)))

                        (pause! [_])

                        (resume! [_]))
                      config)))
