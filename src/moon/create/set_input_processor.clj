(ns moon.create.set-input-processor
  (:require [clojure.gdx.app :as app]))

(defn step
  [{:keys [ctx/app
           ctx/stage]
    :as ctx}]
  (app/set-input-processor! app stage)
  ctx)
