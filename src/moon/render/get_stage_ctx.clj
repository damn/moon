(ns moon.render.get-stage-ctx
  (:require [moon.ui :as ui]))

(defn do!
  [{:keys [ctx/stage]
    :as ctx}]
  (or (ui/get-ctx stage)
      ctx)) ; first render stage does not have ctx set.
