(ns clojure.tx.spawn-line)

(defn do!
  [_ctx {:keys [start end duration color thick?]}]
  [[:tx/spawn-effect
    start
    {:entity/line-render {:thick? thick? :end end :color color}
     :entity/delete-after-duration duration}]])
