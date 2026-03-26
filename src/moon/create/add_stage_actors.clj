(ns moon.create.add-stage-actors
  (:require [moon.stage :as stage]))

(defn step
  [ctx actor-fns]
  (doseq [actor (map (fn [[f & params]] (apply f ctx params)) actor-fns)]
    (stage/add-actor! (:ctx/stage ctx) actor))
  ctx)
