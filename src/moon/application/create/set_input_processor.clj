(ns moon.application.create.set-input-processor
  (:require [com.badlogic.gdx.input :as input]))

(defn step
  [{:keys [ctx/app
           ctx/stage]
    :as ctx}]
  (input/set-processor! (com.badlogic.gdx.Application/.getInput app) stage)
  ctx)
