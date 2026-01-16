(ns moon.render.get-stage-ctx)

(defn do!
  [{:keys [ctx/stage]
    :as ctx}]
  (or (.ctx stage)
      ctx)) ; first render stage does not have ctx set.
