(ns game.render.set-cursor
  (:require [game.ctx :as ctx]))

(defn step
  [ctx]
  (ctx/set-cursor! ctx)
  ctx)
