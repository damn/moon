(ns game.ctx.graphics-delta-time
  (:require [com.badlogic.gdx.graphics :as graphics]))

(defn graphics-delta-time
  [{:keys [ctx/graphics]}]
  (graphics/delta-time graphics))
