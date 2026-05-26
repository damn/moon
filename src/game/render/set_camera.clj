(ns game.render.set-camera
  (:require [game.ctx :as ctx]))

(defn step
  [ctx]
  (ctx/set-camera-position! ctx)
  ctx)
