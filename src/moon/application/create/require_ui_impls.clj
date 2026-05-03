(ns moon.application.create.require-ui-impls)

(defn step [ctx namespaces]
  (run! require namespaces)
  ctx)
