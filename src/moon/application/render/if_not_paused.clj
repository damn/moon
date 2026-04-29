(ns moon.application.render.if-not-paused
  (:require moon.application.render.if-not-paused.tick-entities
            moon.application.render.if-not-paused.update-time
            moon.application.render.if-not-paused.update-potential-fields))

(defn step
  [{:keys [ctx/paused?]
    :as ctx}]
  (if paused?
    ctx
    (-> ctx
        moon.application.render.if-not-paused.update-time/step
        moon.application.render.if-not-paused.update-potential-fields/step
        moon.application.render.if-not-paused.tick-entities/step)))
