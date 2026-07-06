(ns ctx.stage
  (:require
            [com.badlogic.gdx.input :as input] [scene2d.stage :as stage]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (input/set-input-processor! input stage)
    stage))
