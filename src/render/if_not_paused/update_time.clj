(ns render.if-not-paused.update-time
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.graphics :as graphics]))

(defn f
  [{:keys [ctx/app
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (graphics/delta-time (app/graphics app)) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
