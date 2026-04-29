(ns moon.application.render.if-not-paused
  (:require moon.if-not-paused.tick-entities
            moon.if-not-paused.update-time
            moon.if-not-paused.update-potential-fields))

(defn step
  [{:keys [ctx/paused?]
    :as ctx}]
  (if paused?
    ctx
    (-> ctx
        moon.if-not-paused.update-time/step
        moon.if-not-paused.update-potential-fields/step
        moon.if-not-paused.tick-entities/step)))
