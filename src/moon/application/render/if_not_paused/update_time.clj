(ns moon.application.render.if-not-paused.update-time
  (:require [moon.app :as app]))

(defn step
  [{:keys [ctx/app
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (app/delta-time app) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
