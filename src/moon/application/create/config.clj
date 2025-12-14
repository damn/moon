(ns moon.application.create.config)

(defn step [ctx value]
  (assoc ctx :ctx/config value))
