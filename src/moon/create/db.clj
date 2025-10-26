(ns moon.create.db)

(defn step [ctx impl]
  (assoc ctx :ctx/db (impl)))
