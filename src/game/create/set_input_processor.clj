(ns game.create.set-input-processor
  (:require [gdl.app :as app]
            [gdl.input :as input]))

(defn step
  [{:keys [ctx/app
           ctx/stage]
    :as ctx}]
  (input/set-processor! (app/input app) stage)
  ctx)
