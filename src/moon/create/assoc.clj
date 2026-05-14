(ns moon.create.assoc)

(defn step [ctx k f & params]
  (assoc ctx k (apply f ctx params)))
