(ns render.if-not-paused.update-time
  (:require [clojure.gdx.graphics.get-delta-time :as get-delta-time]))

(defn f
  [{:keys [ctx/graphics
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (get-delta-time/f graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
