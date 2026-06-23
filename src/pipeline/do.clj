(ns pipeline.do)

(defn step [ctx [f! & params]]
  (apply f! ctx params)
  ctx)
