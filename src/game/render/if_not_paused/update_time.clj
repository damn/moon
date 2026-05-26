(ns game.render.if-not-paused.update-time
  (:require [game.ctx :as ctx]))

(defn step
  [{:keys [ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (ctx/delta-time ctx) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
