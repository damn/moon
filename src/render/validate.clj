(ns render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

; Try only to be used here
; then make ctx protocols
; then see what's used together
; and data abstraction
(def schema
  (m/schema
   [:map {:closed true}
    ; Input, Audio, Files, Graphics
    [:ctx/app :some] ; ~~ only used in this ns ~~ ✅

    ; Audio, Files
    [:ctx/audio :some] ; ~~ only used in this ns ~~ ✅

    ; Graphics
    [:ctx/batch :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/cursors :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/default-font :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/unit-scale :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/world-unit-scale :some] ; ~~ only used in this ns ~~ ✅ -- accessed through ctx/world-unit-scale protocol
    [:ctx/world-viewport :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/shape-drawer :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/shape-drawer-texture :some] ; ~~ only used in this ns ~~ ✅
    [:ctx/textures :some] ; 💥⚠️

    ; UI
    [:ctx/skin :some] ; 💥⚠️
    [:ctx/stage :some] ; 💥⚠️

    ; Frame
    [:ctx/active-entities :any] ; 💥⚠️
    [:ctx/delta-time :any] ; 💥⚠️
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

    ; Game
    ; The 'game' could be a separate library with no libgdx dependencies?
    [:ctx/content-grid :some]
    [:ctx/entity-ids :some]
    [:ctx/explored-tile-corners :some]
    [:ctx/factions-iterations :some]
    [:ctx/grid :some]
    [:ctx/id-counter :some]
    [:ctx/potential-field-cache :some]
    [:ctx/raycaster :some]
    [:ctx/start-position :some]
    [:ctx/tiled-map :some]
    [:ctx/db :some] ; used here & @ schema....
    [:ctx/elapsed-time :some] ; effect, info, entity, state
    [:ctx/paused? :some] ; only here ✅
    [:ctx/player-eid :some] ; used @ info & entity ⚠️
    ]))

(defn step [ctx]
  (mu/validate-humanize schema ctx)
  ctx)
