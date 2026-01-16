(ns moon.create.stage
  (:import (moon Stage)))

(defn step
  [{:keys [ctx/batch
           ctx/ui-viewport]
    :as ctx}]
  (assoc ctx :ctx/stage (Stage. ui-viewport batch)))
