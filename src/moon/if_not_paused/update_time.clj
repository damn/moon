(ns moon.if-not-paused.update-time
  (:require [gdl.graphics :as graphics]))

(defn do!
  [{:keys [ctx/graphics
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (graphics/delta-time graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
