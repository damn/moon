(ns moon.render.get-stage-ctx
  (:require [moon.stage :as stage]))

(defn do!
  [{:keys [ctx/stage]
    :as ctx}]
  (or (stage/ctx stage)
      ctx)) ; first render stage does not have ctx set.
