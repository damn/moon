(ns moon.application.create
  (:require [clojure.audio :as audio]
            [clojure.files :as files]
            [clojure.files.file-handle :as file-handle]
            [clojure.graphics.freetype :as freetype]
            [clojure.gdx :as gdx]
            [clojure.gdx.graphics.texture :as texture]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.orthographic-camera :as orthographic-camera]
            [clojure.gdx.shape-drawer :as shape-drawer]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.scene2d.ui.skin :as skin]
            [clojure.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [clojure.graphics :as graphics]
            [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.color :as color]
            [clojure.input :as gdx-input]
            [clojure.string :as str]
            [moon.db :as db]
            [moon.input :as input]
            [moon.malli :as m]
            [moon.start :refer [edn-resource]]
            [clojure.math.vector2 :as v])
  (:import (com.badlogic.gdx Input)))

(defn- create-cursor
  [files
   graphics
   path-format
   [path [hotspot-x hotspot-y]]]
  (graphics/new-cursor graphics
                       (files/internal files (format path-format path))
                       hotspot-x
                       hotspot-y))

(extend-type Input
  input/Input
  (key-pressed? [input key]
    (gdx-input/key-pressed? input key))

  (key-just-pressed? [input key]
    (gdx-input/key-just-pressed? input key))

  (button-just-pressed? [input button]
    (gdx-input/button-just-pressed? input button))

  (mouse-position [input]
    (gdx-input/mouse-position input))

  (player-movement-vector [input]
    (let [r (when (input/key-pressed? input :input.keys/d) [1  0])
          l (when (input/key-pressed? input :input.keys/a) [-1 0])
          u (when (input/key-pressed? input :input.keys/w) [0  1])
          d (when (input/key-pressed? input :input.keys/s) [0 -1])]
      (when (or r l u d)
        (let [v (v/add-vs (remove nil? [r l u d]))]
          (when (pos? (v/length v))
            v))))))

(def black [0 0 0 1])
(def white [1 1 1 1])
(def gray  [0.5 0.5 0.5 1])
(def red   [1 0 0 1])

(def outline-alpha 0.4)

(defn- hpbar-color [ratio]
  (let [ratio (float ratio)
        color (cond
               (> ratio 0.75) :green
               (> ratio 0.5)  :darkgreen
               (> ratio 0.25) :yellow
               :else          :red)]
    (color {:green     (color/float-bits [0 0.8 0 1])
            :darkgreen (color/float-bits [0 0.5 0 1])
            :yellow    (color/float-bits [0.5 0.5 0 1])
            :red       (color/float-bits [0.5 0 0 1])})))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/app :some]
    [:ctx/schema :some]
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

(defn- load-colors []
  {
   :colors/mouseover-tile-air  (color/float-bits [1 1 0 0.5])
   :colors/mouseover-tile-none (color/float-bits [1 0 0 0.5])
   :colors/debug-body-outline-collides (color/float-bits white)
   :colors/debug-body-outline (color/float-bits gray)
   :colors/debug-body-outline-render-error (color/float-bits red)
   :colors/debug-cell-entities (color/float-bits [1 0 0 0.6])
   :colors/debug-cell-occupied (color/float-bits [0 0 1 0.6])
   :colors/debug-potential-field (fn [ratio]
                                   (color/float-bits [ratio (- 1 ratio) ratio 0.6]))
   :colors/target-all-line (color/float-bits [1 0 0 0.75])
   :colors/target-all-render (color/float-bits [1 0 0 0.5])
   :colors/target-entity-line (color/float-bits [1 0 0 0.75])
   :colors/target-entity-in-range (color/float-bits [1 0 0 0.5])
   :colors/target-entity-not-in-range (color/float-bits [1 1 0 0.5])
   :colors/enemy-color (color/float-bits [1 0 0 outline-alpha])
   :colors/friendly-color (color/float-bits [0 1 0 outline-alpha])
   :colors/neutral-color  (color/float-bits [1 1 1 outline-alpha])
   :colors/hp-bar hpbar-color
   :colors/hp-bar-rect (color/float-bits black)
   :colors/temp-modifier (color/float-bits [0.5 0.5 0.5 0.4])
   :colors/active-skill-circle (color/float-bits [1 1 1 0.125])
   :colors/active-skill-sector (color/float-bits [1 1 1 0.5])
   :colors/stunned (color/float-bits [1 1 1 0.6])
   :colors/explored-tile (color/float-bits [0.5 0.5 0.5 1])
   :colors/visible-tile (color/float-bits [1 1 1 1])
   :colors/invisible-tile (color/float-bits [0 0 0 1])
   :colors/droppable-item (color/float-bits [0 0.6 0 0.8 1])
   :colors/not-allowed-drop-item (color/float-bits [0.6 0 0 0.8 1])
   :colors/item-rect (color/float-bits [0.5 0.5 0.5 1])
   }
  )

(defn do!
  [create-fns]
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          (let [batch (sprite-batch/create)
                graphics (gdx/graphics)
                files (gdx/files)
                ui-viewport (fit-viewport/create 1440 900 (orthographic-camera/create))
                stage (stage/create ui-viewport batch)
                input (gdx/input)
                shape-drawer-texture (graphics/white-pixel-texture graphics)
                world-unit-scale (float (/ 48))
                ]
            (gdx-input/set-processor! input stage)
            {
             :ctx/schema schema
             :ctx/app      (gdx/app)
             :ctx/audio    (let [{:keys [sound-names path-format]} {:sound-names (edn-resource "sounds.edn")
                                                                    :path-format "sounds/%s.wav"}]
                             (let [sound-name->file-handle (into {}
                                                                 (for [sound-name sound-names
                                                                       :let [path (format path-format sound-name)]]
                                                                   [sound-name
                                                                    (files/internal files path)]))]
                               (into {}
                                     (for [[sound-name file-handle] sound-name->file-handle]
                                       [sound-name
                                        (audio/new-sound (gdx/audio) file-handle)]))))
             :ctx/graphics  graphics
             :ctx/files     files
             :ctx/input     input
             :ctx/batch batch
             :ctx/shape-drawer-texture shape-drawer-texture
             :ctx/shape-drawer (shape-drawer/create batch (texture/region shape-drawer-texture 1 0 1 1))
             :ctx/ui-viewport ui-viewport
             :ctx/stage stage
             :ctx/skin (let [skin (skin/create (files/internal files "uiskin.json"))]
                         (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                         skin)

             :ctx/cursors (let [{:keys [data path-format]} (edn-resource "cursors.edn")]
                            (update-vals data (partial create-cursor files graphics path-format)))

             :ctx/textures (let [{:keys [folder extensions]} {:folder "resources/"
                                                              :extensions #{"png" "bmp"}}]
                             (into {} (for [path (map (fn [path]
                                                        (str/replace-first path folder ""))
                                                      (file-handle/recursively-search (files/internal files folder) extensions))]
                                        [path (texture/create path)])))

             :ctx/world-unit-scale world-unit-scale
             :ctx/world-viewport (let [world-width (* 1440 world-unit-scale)
                                       world-height (* 900 world-unit-scale)]
                                   (fit-viewport/create world-width
                                                        world-height
                                                        (doto (orthographic-camera/create)
                                                          (orthographic-camera/set-to-ortho! false world-width world-height))))

             :ctx/default-font (let [{:keys [path params]} {:path "exocet/films.EXL_____.ttf"
                                                            :params {:size 16
                                                                     :quality-scaling 2
                                                                     :enable-markup? true
                                                                     :use-integer-positions? false
                                                                     ; :texture-filter/linear because scaling to world-units
                                                                     :min-filter :linear
                                                                     :mag-filter :linear}}
                                     {:keys [size
                                             quality-scaling
                                             enable-markup?
                                             use-integer-positions?]} params]
                                 (doto (freetype/generate-font (gdx/app)
                                                               (files/internal files path)
                                                               {:size (* size quality-scaling)})
                                   (bitmap-font/set-scale! (/ quality-scaling))
                                   (bitmap-font/enable-markup! enable-markup?)
                                   (bitmap-font/use-integer-positions! use-integer-positions?)))

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
             :ctx/colors (load-colors)
             :ctx/db (db/create {:schemas "schema.edn"
                                 :properties "properties.edn"})

             :ctx/active-entities nil
             :ctx/delta-time nil

             :ctx/mouseover-eid nil

             :ctx/ui-mouse-position nil
             :ctx/world-mouse-position nil

             :ctx/elapsed-time 0
             :ctx/paused? false
             :ctx/unit-scale (atom 1)
             :ctx/factions-iterations {:good 15 :evil 5}
             :ctx/max-delta 0.04
             :ctx/minimum-size 0.39
             :ctx/z-orders [:z-order/on-ground
                            :z-order/ground
                            :z-order/flying
                            :z-order/effect]
             })
          create-fns))
