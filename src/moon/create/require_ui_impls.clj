(ns moon.create.require-ui-impls)

(defn step [ctx namespaces]
  (run! require namespaces)
  ctx)
