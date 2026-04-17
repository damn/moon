(ns moon.render.clear-screen
  (:require [clojure.graphics :as graphics]))

(defn do!
  [ctx]
  (graphics/clear! (:ctx/graphics ctx) 0 0 0 0)
  ctx)
