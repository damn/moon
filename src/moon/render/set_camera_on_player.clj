(ns moon.render.set-camera-on-player
  (:require [moon.graphics :as graphics]))

(defn do!
  [{:keys [ctx/graphics
           ctx/player-eid]
    :as ctx}]
  (graphics/set-position! graphics (:body/position (:entity/body @player-eid)))
  ctx)
