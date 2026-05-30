(ns create.stage
  (:require [game.app :as app]
            [gdx.scenes.scene2d.stage :as stage]
            [gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage
         (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
           (app/set-input-processor! app stage)
           stage)))
