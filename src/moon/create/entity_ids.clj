(ns moon.create.entity-ids)

(defn step [ctx]
  (assoc ctx :ctx/entity-ids (atom {})))
