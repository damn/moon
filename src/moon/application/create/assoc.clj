(ns moon.application.create.assoc)

(defn step [ctx k f]
  (assoc ctx k (f ctx)))
