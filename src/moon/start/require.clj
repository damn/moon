(ns moon.start.require)

(defn step [namespaces]
  (run! require namespaces))
