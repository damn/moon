(ns create.add-stage-actors
  (:require [gdx.stage :as stage]))

(defn step
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [[f & params] actor-fns]
    (stage/add-actor! stage (apply f ctx params)))
  ctx)
