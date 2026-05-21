(ns game.render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

(def schema
  (m/schema
   [:map {:closed true}
    ; GDX app
    [:ctx/app :some]
    [:ctx/audio :some]
    [:ctx/batch :some]
    [:ctx/cursors :some]
    [:ctx/default-font :some]
    [:ctx/skin :some]
    [:ctx/stage :some]
    [:ctx/unit-scale :some]
    [:ctx/world-unit-scale :some]
    [:ctx/world-viewport :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/textures :some]

    ; Frame
    [:ctx/active-entities :any]
    [:ctx/delta-time :any]
    [:ctx/mouseover-eid :any]
    [:ctx/ui-mouse-position :any]
    [:ctx/world-mouse-position :any]

    ; Constants
    [:ctx/colors :some]
    [:ctx/controls :some]
    [:ctx/controls-info :some]
    [:ctx/max-delta :some]
    [:ctx/max-speed :some]
    [:ctx/minimum-size :some]
    [:ctx/render-z-order :some]
    [:ctx/z-orders :some]

    ; Game (level, time, db ?)
    ; The 'game' could be a separate library with no ligdx dependencies (which it is already)
    [:ctx/content-grid :some]
    ; :ctx/player-eid
    ; :ctx/grid

    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/potential-field-cache :some]
    [:ctx/db :some]
    [:ctx/elapsed-time :some]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]
    ]))

(defn step [ctx]
  (mu/validate-humanize schema ctx)
  ctx)
