(ns moon.application.render.clear-screen
  (:require [moon.graphics :as graphics]))

(defn step [ctx]
  (graphics/clear! (:ctx/app ctx) 0 0 0 0)
  ctx)
