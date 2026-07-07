(ns clojure.update-time
  (:require [clojure.graphics :as graphics]
            [clojure.max-delta :as max-delta]))

(defn f
  [{:keys [ctx/graphics]
    :as ctx}]
  (let [delta-ms (min (graphics/get-delta-time graphics) max-delta/max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))
