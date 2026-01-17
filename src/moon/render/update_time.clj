(ns moon.render.update-time
  (:import (com.badlogic.gdx Graphics)))

(defn do!
  [{:keys [ctx/graphics
           ctx/paused?
           ctx/world]
    :as ctx}]
  (if paused?
    ctx
    (let [delta-ms (min (Graphics/.getDeltaTime graphics) (:world/max-delta world))]
      (-> ctx
          (assoc :ctx/delta-time delta-ms)
          (update :ctx/elapsed-time + delta-ms)))))
