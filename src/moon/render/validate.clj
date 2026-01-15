(ns moon.render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/db :some]
    [:ctx/world :some]]
   ; player-eid
   ; mouesover-eid
   ; paused?
   ; textures
   ; world-mouse-position
   ; ui-mouse-position
   ; elapsed-time ? delta-time ?
   ; effect/entity/etc. not receive world but ctx
   ))

(defn do! [ctx]
  (mu/validate-humanize schema ctx)
  ctx)
