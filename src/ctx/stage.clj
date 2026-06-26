(ns ctx.stage
  (:require [input.set-processor :as set-processor!]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [scene2d.stage :as stage]))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (set-processor!/f input stage)
    stage))
