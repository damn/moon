(ns effects.applicable.target-entity
  (:require [game.effect :as effect]))

(defn f
  [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
  (and target
       (seq (filter #(effect/applicable? % effect-ctx) entity-effects))))
