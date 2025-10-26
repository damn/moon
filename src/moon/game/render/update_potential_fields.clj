(ns moon.game.render.update-potential-fields
  (:require [moon.world :as world]))

(defn step
  [{:keys [ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do
     (world/update-potential-fields! world)
     ctx)))
