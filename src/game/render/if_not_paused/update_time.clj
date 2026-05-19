(ns game.render.if-not-paused.update-time
  (:require [gdl.app :as app]
            [gdl.graphics :as graphics]))

(defn step
  [{:keys [ctx/app
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (graphics/delta-time (app/graphics app)) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
