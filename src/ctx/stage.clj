(ns ctx.stage
  (:require [clojure.input :as input]
            [gdx.scene2d.stage :as stage]
            [clojure.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (input/set-input-processor! input stage)
    stage))
