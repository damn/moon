(ns moon.application.render.set-camera-on-player
  (:require [moon.graphics :as graphics]
            [moon.world :as world]))

(defn step
  [{:keys [ctx/graphics
           ctx/world]
    :as ctx}]
  (graphics/set-position! graphics (world/player-position world))
  ctx)
