(ns ctx.graphics-delta-time
  (:require [graphics.delta-time :as delta-time]))

(defn graphics-delta-time
  [{:keys [ctx/graphics]}]
  (delta-time/f graphics))
