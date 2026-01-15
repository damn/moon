(ns moon.render.update-potential-fields
  (:require [moon.world :as world]))

(defn do!
  [{:keys [ctx/world]
    :as ctx}]
  (if (:world/paused? world)
    ctx
    (do
     (world/update-potential-fields! world)
     ctx)))
