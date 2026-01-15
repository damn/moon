(ns moon.render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/mouseover-eid :any] ; can be nil
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/db :some]
    [:ctx/player-eid :some]
    [:ctx/world :some]]))

(defn do! [ctx]
  (mu/validate-humanize schema ctx)
  ctx)
