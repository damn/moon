(ns moon.render.validate
  (:require [moon.game :as game]
            [moon.malli :as m]))

(defn do! [ctx]
  (m/validate-humanize game/schema ctx)
  ctx)
