(ns cdq.create.input
  (:require [moon.app :as app]))

(defn step [{:keys [ctx/app ctx/stage] :as ctx}]
  (app/set-input-processor! app stage)
  (assoc ctx :ctx/input (.getInput app)))
