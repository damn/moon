(ns moon.application.render.validate
  (:require [malli.core :as m]
            [malli.utils :as mu]))

(def ^:private schema
  (m/schema
   [:map {:closed true}
    [:ctx/audio :some]
    [:ctx/config :some]
    [:ctx/files :some]
    [:ctx/graphics :some]
    [:ctx/input :some]
    [:ctx/stage :some]
    [:ctx/skin :some]
    [:ctx/db :some]
    [:ctx/world :some]]))

(defn step [ctx]
  (mu/validate-humanize schema ctx)
  ctx)
