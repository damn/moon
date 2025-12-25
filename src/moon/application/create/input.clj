(ns moon.application.create.input
  (:require [gdl.input :as input]))

(defn step [{:keys [ctx/input
                    ctx/stage] :as ctx}]
  (input/set-processor! input stage)
  (assoc ctx :ctx/input input))
