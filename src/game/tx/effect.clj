(ns game.tx.effect
  (:require [moon.effect :as effect]))

(defn do! [ctx effect-ctx effects]
  (mapcat #(effect/handle % effect-ctx ctx)
          (filter #(effect/applicable? % effect-ctx) effects)))
