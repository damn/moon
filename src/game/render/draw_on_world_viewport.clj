(ns game.render.draw-on-world-viewport
  (:require [game.ctx :as ctx]))

(defn step
  [ctx draw-fns]
  (ctx/draw-on-world-viewport! ctx draw-fns)
  ctx)
