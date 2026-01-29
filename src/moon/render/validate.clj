(ns moon.render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/active-entities :any]
    [:ctx/audio :some]
    [:ctx/colors :some]
    [:ctx/cursors :some]
    [:ctx/default-font :some]
    [:ctx/batch :some]
    [:ctx/files :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/unit-scale :some]
    [:ctx/world-unit-scale :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/mouseover-eid :any]
    [:ctx/player-eid :some]
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/textures :some]
    [:ctx/db :some]
    [:ctx/paused? :some]
    [:ctx/elapsed-time :some]
    [:ctx/delta-time :any]
    [:ctx/ui-viewport :some]
    [:ctx/ui-mouse-position :any]
    [:ctx/world-viewport :some]
    [:ctx/world-mouse-position :any]
    [:ctx/factions-iterations :some]
    [:ctx/max-delta :some]
    [:ctx/minimum-size :some]
    [:ctx/z-orders :some]
    [:ctx/max-speed :some]
    [:ctx/render-z-order :some]
    [:ctx/tiled-map :some]
    [:ctx/start-position :some]
    [:ctx/grid :some]
    [:ctx/content-grid :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/raycaster :some]
    [:ctx/potential-field-cache :some]
    [:ctx/id-counter :some]
    [:ctx/entity-ids :some]
    ]))

(defn do! [ctx]
  (mu/validate-humanize schema ctx)
  ctx)
