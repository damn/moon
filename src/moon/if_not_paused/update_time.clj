(ns moon.if-not-paused.update-time
  (:require [clojure.graphics :as graphics]))

(defn step
  [{:keys [ctx/graphics
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (graphics/delta-time graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
