(ns moon.start.require)

(defn step [ctx namespaces]
  (run! require namespaces)
  ctx)
