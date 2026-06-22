(ns effects.applicable.target-entity
  (:require [moon.effect.is-applicable :as applicable?]))

(defn f
  [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
  (and target
       (seq (filter #(applicable?/f % effect-ctx) entity-effects))))
