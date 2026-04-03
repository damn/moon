(ns moon.create.actor-fns)

(defn step [ctx k->fn]
  (assoc ctx :ctx/actor-fns k->fn))
