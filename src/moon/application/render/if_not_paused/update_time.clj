(ns moon.application.render.if-not-paused.update-time
  (:require [moon.graphics :as graphics]))

(defn step
  [{:keys [ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (graphics/delta-time ctx) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
