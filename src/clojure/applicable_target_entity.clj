(ns clojure.applicable-target-entity
  (:require [clojure.is-applicable :as applicable?]))

(defn f
  [[_ {:keys [entity-effects]}] {:keys [effect/target] :as effect-ctx}]
  (and target
       (seq (filter #(applicable?/f % effect-ctx) entity-effects))))
