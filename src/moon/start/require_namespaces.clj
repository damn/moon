(ns moon.start.require-namespaces)

(defn step [ctx ns-symbols]
  (run! require ns-symbols)
  ctx)
