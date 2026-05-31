(ns create.stage
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]
            [com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]
            [gdx.stage :as stage]))

(defn step
  [{:keys [ctx/app
           ctx/batch]
    :as ctx}]
  (assoc ctx :ctx/stage
         (let [stage (stage/create (fit-viewport/create 1440 900) batch)]
           (input/set-processor! (app/input app) stage)
           stage)))
