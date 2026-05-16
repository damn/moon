(ns moon.create.set-input-processor
  (:require [clojure.gdx.app :as app]
            [clojure.input :as input]))

(defn step
  [{:keys [ctx/app
           ctx/stage]
    :as ctx}]
  (input/set-processor! (app/input app) stage)
  ctx)
