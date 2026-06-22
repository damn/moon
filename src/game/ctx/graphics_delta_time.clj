(ns game.ctx.graphics-delta-time
  (:require [gdl.delta-time :as delta-time]))

(defn graphics-delta-time
  [{:keys [ctx/graphics]}]
  (delta-time/f graphics))
