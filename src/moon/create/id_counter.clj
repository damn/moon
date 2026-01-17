(ns moon.create.id-counter)

(defn step [ctx]
  (assoc ctx :ctx/id-counter (atom 0)))
