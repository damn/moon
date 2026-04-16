(ns moon.render.validate
  (:require [moon.malli :as m]))

(defn do! [ctx]
  (m/validate-humanize (:ctx/schema ctx) ctx)
  ctx)
