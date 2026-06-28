(ns ctx.stage
  (:require [input.set-processor :as set-processor!]
            [scene2d.stage :as stage]
            [viewport.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (set-processor!/f input stage)
    stage))
