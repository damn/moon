(ns clojure.get-stage-ctx)

(defn step
  [{:keys [ctx/stage]
    :as ctx}]
  (or (:stage/ctx stage)
      ctx)) ; first render stage does not have ctx set.
