(ns moon.create.set-input-processor
  (:require [clj.api.com.badlogic.gdx.input :as input]))

(defn step
  [{:keys [ctx/input
           ctx/stage]
    :as ctx}]
  (input/set-processor! input stage)
  ctx)
