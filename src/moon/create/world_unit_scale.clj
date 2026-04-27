(ns moon.create.world-unit-scale)

(defn step [ctx]
  (assoc ctx :ctx/world-unit-scale (float (/ 48))))
