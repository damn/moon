(ns moon.game.render.remove-destroyed-entities
  (:require [moon.world :as world]
            [moon.txs :as txs]))

(defn step
  [{:keys [ctx/world]
    :as ctx}]
  (txs/handle! ctx (world/remove-destroyed-entities! world))
  ctx)
