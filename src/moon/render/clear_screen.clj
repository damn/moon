(ns moon.render.clear-screen
  (:require [clojure.graphics :as graphics]))

(defn step [ctx]
  (graphics/clear! (:ctx/graphics ctx) 0 0 0 0)
  ctx)
