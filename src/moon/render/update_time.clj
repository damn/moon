(ns moon.render.update-time
  (:require [moon.graphics :as graphics]
            [moon.world :as world]))

(defn do!
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (if (:world/paused? (:ctx/world ctx))
    ctx
    (update ctx :ctx/world world/update-time (graphics/delta-time graphics))))
