(ns moon.game.render.dissoc-interaction-state)

(defn step [ctx]
  (dissoc ctx :ctx/interaction-state))
