(ns moon.render.clear-screen
  (:require [gdl.graphics :as graphics]))

(defn do!
  [ctx]
  (graphics/clear! (:ctx/graphics ctx) 0 0 0 0)
  ctx)
