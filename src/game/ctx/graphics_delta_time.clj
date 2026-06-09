(ns game.ctx.graphics-delta-time
  (:require [com.badlogic.gdx.graphics.delta-time :as delta-time]))

(defn graphics-delta-time
  [{:keys [ctx/graphics]}]
  (delta-time/f graphics))
