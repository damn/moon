(ns render.if-not-paused.update-time
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [ctx/graphics
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (gdx/graphics-get-delta-time graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
