(ns moon.create.set-input-processor
  (:require [gdl.input :as input]))

(defn step
  [{:keys [ctx/input
           ctx/stage]
    :as ctx}]
  (input/set-processor! input stage)
  ctx)
