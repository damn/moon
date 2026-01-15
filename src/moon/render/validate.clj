(ns moon.render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/cursors :some]
    [:ctx/default-font :some]
    [:ctx/batch :some]
    [:ctx/shape-drawer :some]
    [:ctx/shape-drawer-texture :some]
    [:ctx/unit-scale :some]
    [:ctx/world-unit-scale :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/mouseover-eid :any]
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/textures :some]
    [:ctx/db :some]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    [:ctx/ui-viewport :some]
    [:ctx/ui-mouse-position :any]
    [:ctx/world :some]
    [:ctx/world-viewport :some]
    [:ctx/world-mouse-position :any]
    ]))

(defn do! [ctx]
  (mu/validate-humanize schema ctx)
  ctx)
