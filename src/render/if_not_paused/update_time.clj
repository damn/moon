(ns render.if-not-paused.update-time
  (:import (com.badlogic.gdx Graphics)))

(defn f
  [{:keys [ctx/graphics
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (Graphics/.getDeltaTime graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
