(ns clojure.effect
  (:require [clojure.handle :as handle]
            [clojure.is-applicable :as applicable?]))

(defn do! [ctx effect-ctx effects]
  (mapcat #(handle/f % effect-ctx ctx)
          (filter #(applicable?/f % effect-ctx) effects)))
