(ns clojure.ctx.stage
  (:require [clojure.input.set-processor :as set-processor!]
            [clojure.fit-viewport :as fit-viewport]
            [clojure.stage :as stage]))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (set-processor!/f input stage)
    stage))
