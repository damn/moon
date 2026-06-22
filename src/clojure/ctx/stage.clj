(ns clojure.ctx.stage
  (:require [gdl.set-processor :as set-processor!]
            [gdl.fit-viewport :as fit-viewport]
            [gdl.stage :as stage]))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (set-processor!/f input stage)
    stage))
