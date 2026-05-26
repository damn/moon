(ns game.render.validate
  (:require [game.ctx :as ctx]))

(defn step [ctx]
  (ctx/validate ctx)
  ctx)
