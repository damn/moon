(ns clojure.map.recur-sort)

(defn recur-sort [m]
  (into (sorted-map)
        (zipmap (keys m)
                (map #(if (map? %)
                        (recur-sort %)
                        %)
                     (vals m)))))
