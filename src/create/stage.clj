(ns create.stage
  (:require [com.badlogic.gdx.input.set-processor :as set-processor!]
            [com.badlogic.gdx.utils.fit-viewport :as fit-viewport]
            [com.badlogic.gdx.scenes.scene2d.stage :as stage]))

(defn step
  [{:keys [ctx/input
           ctx/batch]}]
  (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
    (set-processor!/f input stage)
    stage))
