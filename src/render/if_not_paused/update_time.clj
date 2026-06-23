(ns render.if-not-paused.update-time
  (:require [ctx.graphics-delta-time :refer [graphics-delta-time]]))

(defn f
  [{:keys [ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (graphics-delta-time ctx) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
