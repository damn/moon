(ns moon.render.remove-destroyed-entities
  (:require [moon.ctx :as ctx]
            [moon.world :as world]))

(defn do!
  [{:keys [ctx/world]
    :as ctx}]
  (ctx/handle! ctx (world/remove-destroyed-entities! world))
  ctx)
