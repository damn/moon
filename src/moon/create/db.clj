(ns moon.create.db)

(defn step [ctx db-impl]
  (assoc ctx :ctx/db ((requiring-resolve db-impl))))
