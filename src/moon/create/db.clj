(ns moon.create.db)

(defn step [ctx db-impl]
  (assoc ctx :ctx/db (db-impl)))
