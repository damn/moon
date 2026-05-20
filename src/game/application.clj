(ns game.application
  (:require [clojure.config :refer [edn-resource]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [gdx.api :as gdx]
            [game.textures]
            [gdl.app :as app]
            [gdl.application-listener :as listener]
            [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.files.file-handle :as file-handle]
            [gdl.graphics :as graphics]
            [gdl.graphics.batch :as batch]
            [gdl.graphics.texture :as texture]
            [gdl.graphics.pixmap :as pixmap]
            [gdl.graphics.texture.filter :as texture.filter]
            [gdl.graphics.g2d.bitmap-font :as bitmap-font]
            [gdl.graphics.g2d.bitmap-font.data :as font.data]
            [gdl.graphics.g2d.freetype.font-generator :as font-generator]
            [gdl.input :as input]
            [gdl.scene2d.ui.skin :as skin])
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
                          (gdx/put-colors! {"PRETTY_NAME" [0.84 0.8 0.52 1]})
                          (gdx/tooltip-manager-set-initial-time! 0)
                          (reset! state
                                  (reduce (fn [ctx [f & params]]
                                            (apply f ctx params))
                                          (let [batch (gdx/sprite-batch)
                                                white-pixel-texture (let [pixmap (doto (gdx/pixmap 1 1)
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
                                                                   (gdx/fit-viewport world-width
                                                                                     world-height
                                                                                     (gdx/orthographic-camera
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
                                             :ctx/stage (let [stage (gdx/stage (gdx/fit-viewport 1440 900) batch)]
                                                          (input/set-processor! (app/input app) stage)
                                                          stage)
                                             :ctx/skin (let [skin (file-handle/skin (files/internal (app/files app) "uiskin.json"))]
                                                         (-> skin
                                                             (skin/font "default-font")
                                                             bitmap-font/data
                                                             (font.data/set-markup-enabled! true))
                                                         skin)
                                             :ctx/unit-scale (atom 1)
                                             :ctx/textures (game.textures/create (app/files app))})
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
