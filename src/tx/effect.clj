(ns tx.effect
  (:require [moon.effect.handle :as handle]
            [moon.effect.is-applicable :as applicable?]))

(defn do! [ctx effect-ctx effects]
  (mapcat #(handle/f % effect-ctx ctx)
          (filter #(applicable?/f % effect-ctx) effects)))
