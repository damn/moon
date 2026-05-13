(ns moon.application.create.set-input-processor
  (:require [moon.app :as app]))

(defn step
  [{:keys [ctx/app
           ctx/stage]
    :as ctx}]
  (app/set-input-processor! app stage)
  ctx)
