(ns game.reaction-txs.sound
  (:require [game.ctx :as ctx]))

(defn do!
  [ctx sound-name]
  (ctx/play-sound! ctx sound-name)
  ctx)
