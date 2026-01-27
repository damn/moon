(ns moon.render.get-stage-ctx
  (:import (moon Stage)))

(defn do!
  [{:keys [ctx/stage]
    :as ctx}]
  (or (.ctx ^Stage stage)
      ctx)) ; first render stage does not have ctx set.
