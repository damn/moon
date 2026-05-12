(ns moon.application.create.add-stage-actors
  (:require [moon.stage :as stage]))

(defn step
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[actor-fn & params] actor-fns]
    (stage/add-actor! stage (apply actor-fn ctx params)))
  ctx)
