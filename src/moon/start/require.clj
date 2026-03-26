(ns moon.start.require)

(defn step [ctx namespace-symbol]
  (require namespace-symbol)
  ctx)
