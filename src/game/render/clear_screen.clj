(ns game.render.clear-screen
  (:require [game.ctx :as ctx]))

(defn step [ctx]
  (ctx/clear-screen! ctx)
  ctx)
