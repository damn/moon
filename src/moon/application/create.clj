(ns moon.application.create
  (:require [clojure.audio :as audio]
            [clojure.files :as files]
            [clojure.gdx :as gdx]
            [clojure.gdx.graphics.texture :as texture]
            [clojure.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clojure.gdx.shape-drawer :as shape-drawer]
            [clojure.gdx.scene2d.ui.tooltip-manager :as tooltip-manager]
            [clojure.graphics :as graphics]
            [moon.malli :as m]
            [moon.start :refer [edn-resource]]
            ))

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

(defn do!
  [create-fns]
  (tooltip-manager/set-initial-time! 0)
  (reduce (fn [ctx [f & params]]
            (apply f ctx params))
          (let [batch (sprite-batch/create)
                graphics (gdx/graphics)
                files (gdx/files)
                input (gdx/input)
                shape-drawer-texture (graphics/white-pixel-texture graphics)
                ]
            {:ctx/schema schema
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
             })
          create-fns))
