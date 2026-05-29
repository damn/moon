(ns create.stage
  (:require [gdx.application :as app]
            [gdx.input :as input]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage
         (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
           (input/set-processor! (app/input app) stage)
           stage)))
