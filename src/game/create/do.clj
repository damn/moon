(ns game.create.do)

(defn step [ctx [f & params]]
  (apply f params)
  ctx)
