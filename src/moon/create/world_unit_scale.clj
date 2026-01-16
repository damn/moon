(ns moon.create.world-unit-scale)

(defn step [ctx tile-size]
  (assoc ctx :ctx/world-unit-scale (float (/ tile-size))))
