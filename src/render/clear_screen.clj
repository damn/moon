(ns render.clear-screen
  (:require [game.ctx.clear-screen :refer [clear-screen!]]))

(defn step
  [ctx]
  (clear-screen! ctx 0 0 0 0)
  ctx)
