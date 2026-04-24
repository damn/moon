(ns moon.game
  (:require [clojure.audio :as audio]
            [clojure.disposable :as disposable]
            [clojure.edn :as edn]
            [clojure.files :as files]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.gdx.scene2d.ui.tooltip-manager :as tooltip-manager]
            [clojure.gdx.shape-drawer :as shape-drawer]
            [clojure.gdx.orthographic-camera :as orthographic-camera]
            [clojure.gdx.viewport]
            [clojure.graphics :as graphics]
            [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.freetype :as freetype]
            [clojure.graphics.texture :as texture]
            [clojure.graphics.viewport :as viewport]
            [clojure.input :as input]
            [clojure.java.io :as io]
            [clojure.math.vector2 :as v]
            [clojure.string :as str]
            [moon.malli :as m]
            [moon.input]
            moon.impl.textures
            )
  (:import (com.badlogic.gdx Input)))

(def schema
  (m/schema
   [:map {:closed true}
    [:ctx/app :some]
    [:ctx/active-entities :any]
    [:ctx/audio :some]
    [:ctx/batch :some]
    [:ctx/colors :some]
    [:ctx/content-grid :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/cursors :some]
    [:ctx/db :some]
    [:ctx/default-font :some]
    [:ctx/delta-time :any]
    [:ctx/elapsed-time :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/files :some]
    [:ctx/graphics :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/input :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/mouseover-eid :any]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/render-z-order :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/skin :some]
    [:ctx/stage :some]
    [:ctx/start-position :some]
    [:ctx/textures :some]
    [:ctx/tiled-map :some]
    [:ctx/ui-mouse-position :any]
    [:ctx/ui-viewport :some]
    [:ctx/unit-scale :some]
    [:ctx/world-mouse-position :any]
    [:ctx/world-unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/z-orders :some]
    ]))

(defn create!
  [ctx create-fns]
  (tooltip-manager/set-initial-time! 0)
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          (concat
           [
            [(fn [{:keys [ctx/audio ctx/files] :as ctx}]
               (assoc ctx :ctx/audio
                      (into {}
                            (for [sound-name (-> "sounds.edn" io/resource slurp edn/read-string)]
                              [sound-name
                               (audio/new-sound audio (files/internal files (format "sounds/%s.wav" sound-name)))]))))]
            [(fn [ctx]
               (assoc ctx :ctx/batch (sprite-batch/create)))]
            [(fn [{:keys [ctx/graphics] :as ctx}]
               (assoc ctx :ctx/shape-drawer-texture (graphics/white-pixel-texture graphics)))]
            [(fn [{:keys [ctx/batch
                          ctx/shape-drawer-texture]
                   :as ctx}]
               (assoc ctx :ctx/shape-drawer (shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1))))]
            [(fn [ctx]
               (assoc ctx :ctx/ui-viewport (clojure.gdx.viewport/create 1440 900 (orthographic-camera/create))))]
            [(fn [{:keys [ctx/batch
                          ctx/ui-viewport]
                   :as ctx}]
               (assoc ctx :ctx/stage (stage/create ui-viewport batch)))]
            [(fn [{:keys [ctx/input
                          ctx/stage]
                   :as ctx}]
               (input/set-processor! input stage)
               ctx)]
            [(fn [{:keys [ctx/files] :as ctx}]
               (assoc ctx :ctx/skin (let [skin (skin/create (files/internal files "uiskin.json"))]
                                      (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                                      skin)))]
            [(fn [{:keys [ctx/graphics
                          ctx/files]
                   :as ctx}]
               (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                                         (update-vals data
                                                      (fn [[path [hotspot-x hotspot-y]]]
                                                        (graphics/new-cursor graphics
                                                                             (files/internal files (format path-format path))
                                                                             hotspot-x
                                                                             hotspot-y))))))]
            [(fn [ctx]
               (assoc ctx :ctx/textures (moon.impl.textures/create ctx {:folder "resources/"
                                                                        :extensions #{"png" "bmp"}})))]
            [(fn [ctx]
               (assoc ctx :ctx/world-unit-scale (float (/ 48))))]
            [(fn [{:keys [ctx/world-unit-scale] :as ctx}]
               (assoc ctx :ctx/world-viewport
                      (let [world-width  (* 1440  world-unit-scale)
                            world-height (* 900 world-unit-scale)]
                        (clojure.gdx.viewport/create world-width
                                                     world-height
                                                     (doto (orthographic-camera/create)
                                                       (orthographic-camera/set-to-ortho! false world-width world-height))))))]
            [(fn [{:keys [ctx/app
                          ctx/files]
                   :as ctx}]
               (assoc ctx :ctx/default-font (let [{:keys [path
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
                                              (doto (freetype/generate-font app
                                                                            (files/internal files path)
                                                                            {:size (* size quality-scaling)})
                                                (bitmap-font/set-scale! (/ quality-scaling))
                                                (bitmap-font/enable-markup! enable-markup?)
                                                (bitmap-font/use-integer-positions! use-integer-positions?))))
               )]
            [(fn [ctx]
               (assoc ctx
                      :ctx/controls {
                                     :zoom-in :input.keys/minus
                                     :zoom-out :input.keys/equals
                                     :unpause-once :input.keys/p
                                     :unpause-continously :input.keys/space
                                     :close-windows-key :input.keys/escape
                                     :toggle-inventory  :input.keys/i
                                     :toggle-entity-info :input.keys/e
                                     :open-debug-button :input.buttons/right
                                     }
                      :ctx/controls-info (str/join "\n"
                                                   ["[W][A][S][D] - Move"
                                                    "[ESCAPE] - Close windows"
                                                    "[I] - Inventory window"
                                                    "[E] - Entity Info window"
                                                    "[-]/[=] - Zoom"
                                                    "[P]/[SPACE] - Unpause"
                                                    "rightclick on tile or entity - open debug data window"
                                                    "Leftmouse click - use skill/drop item on cursor"])
                      )
               )]
            ]
           create-fns)))

(defn dispose!
  [{:keys [ctx/audio
           ctx/batch
           ctx/cursors
           ctx/default-font
           ctx/shape-drawer-texture
           ctx/skin
           ctx/textures
           ctx/tiled-map]}]
  (run! disposable/dispose! (vals audio))
  (disposable/dispose! batch)
  (run! disposable/dispose! (vals cursors))
  (disposable/dispose! default-font)
  (disposable/dispose! shape-drawer-texture)
  (disposable/dispose! skin)
  (run! disposable/dispose! (vals textures))
  (disposable/dispose! tiled-map)
  nil)

(defn render! [ctx render-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          ctx
          render-fns))

(defn resize!
  [{:keys [ctx/ui-viewport
           ctx/world-viewport]}
   width height]
  (viewport/update! ui-viewport width height true)
  (viewport/update! world-viewport width height false)
  nil)

(extend-type Input
  moon.input/Input
  (key-pressed? [input key]
    (input/key-pressed? input key))

  (key-just-pressed? [input key]
    (input/key-just-pressed? input key))

  (button-just-pressed? [input button]
    (input/button-just-pressed? input button))

  (mouse-position [input]
    (input/mouse-position input))

  (player-movement-vector [input]
    (let [r (when (input/key-pressed? input :input.keys/d) [1  0])
          l (when (input/key-pressed? input :input.keys/a) [-1 0])
          u (when (input/key-pressed? input :input.keys/w) [0  1])
          d (when (input/key-pressed? input :input.keys/s) [0 -1])]
      (when (or r l u d)
        (let [v (v/add-vs (remove nil? [r l u d]))]
          (when (pos? (v/length v))
            v))))))
