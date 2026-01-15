(ns moon.render.update-time
  (:require [moon.graphics :as graphics]
            [moon.world :as world]))

(defn do!
  [{:keys [ctx/graphics
           ctx/paused?]
    :as ctx}]
  (if paused?
    ctx
    (update ctx :ctx/world world/update-time (graphics/delta-time graphics))))
