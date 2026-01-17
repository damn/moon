(ns moon.render.update-time
  (:import (com.badlogic.gdx Graphics)))

(defn do!
  [{:keys [ctx/graphics
           ctx/paused?
           ctx/max-delta]
    :as ctx}]
  (if paused?
    ctx
    (let [delta-ms (min (Graphics/.getDeltaTime graphics) max-delta)]
      (-> ctx
          (assoc :ctx/delta-time delta-ms)
          (update :ctx/elapsed-time + delta-ms)))))
