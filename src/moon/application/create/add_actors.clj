(ns moon.application.create.add-actors
  (:require [gdl.ui.stage :as stage]))

(defn step
  [{:keys [ctx/config
           ctx/stage]
    :as ctx}]
  (doseq [[actor-create-fn & params] (:config/actor-create-fns config)]
    (stage/add-actor! stage (stage/build (apply actor-create-fn ctx params))))
  ctx)
