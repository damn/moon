(ns game.render.draw-tiled-map
  (:require [game.ctx :as ctx]))

(defn step
  [ctx]
  (ctx/draw-tiled-map! ctx)
  ctx)
