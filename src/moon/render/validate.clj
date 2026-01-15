(ns moon.render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

; TODO or at each step we assert what we want ?
; e.g. after create
; or before render, etc.
(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/mouseover-eid :any] ; can be nil
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/textures :some]
    [:ctx/db :some]
    [:ctx/paused? :some]
    [:ctx/player-eid :some]
    [:ctx/ui-viewport :some]
    [:ctx/world :some]
    ; graphics/world-mouse-position
    ; graphics/ui-mouse-position
    ; delta-time / elapsed-time / ?
    ; world/render-z-order ?
    ]))

(defn do! [ctx]
  (mu/validate-humanize schema ctx)
  ctx)
