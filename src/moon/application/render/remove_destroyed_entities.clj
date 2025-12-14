(ns moon.application.render.remove-destroyed-entities
  (:require [moon.txs :as txs]
            [moon.world :as world]))

(defn step
  [{:keys [ctx/world]
    :as ctx}]
  (txs/handle! ctx (world/remove-destroyed-entities! world))
  ctx)
