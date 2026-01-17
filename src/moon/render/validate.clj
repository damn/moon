(ns moon.render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some] ; TODO map of 'sound-name->sound'
    [:ctx/cursors :some] ; map of cursor-key->cursor
    [:ctx/default-font :some] ; font -> API ?
    [:ctx/batch :some] ; batch -> API ?

    [:ctx/files :some]

    [:ctx/shape-drawer :some] ; sd -> API ?
    [:ctx/shape-drawer-texture :some]
    [:ctx/unit-scale :some] ; only relevant during drawing?
    [:ctx/world-unit-scale :some]

    [:ctx/graphics :some] ; 'moon.graphics' -> set-cursor/fps/etc.?

    [:ctx/input :some] ; make protocol

    [:ctx/mouseover-eid :any] ; entity protocol/descirin?
    [:ctx/player-eid :some]   ; entity protocol/descirin?

    [:ctx/stage :some] ; moon.ui remove, moon.stage API ?
    [:ctx/skin :some] ; ?

    [:ctx/textures :some]  ; map of path to Texture

    [:ctx/db :some] ; moon.db ? :schemas?!

    [:ctx/paused? :some]
    [:ctx/elapsed-time :some]
    [:ctx/delta-time :any]

    [:ctx/ui-viewport :some] ; viewport API ?
    [:ctx/ui-mouse-position :any]

    [:ctx/world :some] ; level - level API (not open) ?
    ; TODO : remove :active-entities ?!
    ; remove elapsed time ?
    ; cant relly grep it because world-foo ctxzs

    [:ctx/world-viewport :some]
    [:ctx/world-mouse-position :any]
    ]))

(defn do! [ctx]
  (mu/validate-humanize schema ctx)
  ctx)

; TODO create/render/etc. are all 'transactions' ?
; remove need for ctx/handle!?
; ctx/handle at ui? (see sound play)
