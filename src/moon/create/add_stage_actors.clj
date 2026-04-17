(ns moon.create.add-stage-actors
  (:require [clojure.scene2d.stage :as stage]))

(defn step
  [ctx actor-fns]
  (doseq [actor (map (fn [[f & params]] (apply f ctx params)) actor-fns)]
    (stage/add-actor! (:ctx/stage ctx) actor))
  ctx)
