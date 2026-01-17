(ns moon.create.potential-field-cache)

(defn step [ctx]
  (assoc ctx :ctx/potential-field-cache (atom nil)))
