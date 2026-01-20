(ns moon.create.add-stage-actors
  (:require [moon.stage :as stage]))

(defn step
  [{:keys [ctx/stage]
    :as ctx}
   actor-fns]
  (doseq [actor (map (fn [[f & params]] (apply f ctx params)) actor-fns)]
    (stage/add-actor! stage actor))
  ctx)
