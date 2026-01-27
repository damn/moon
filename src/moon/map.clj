(ns moon.map)

(defn recur-sort [m]
  (into (sorted-map)
        (zipmap (keys m)
                (map #(if (map? %)
                        (recur-sort %)
                        %)
                     (vals m)))))

#_(defn dissoc-in [m ks]
  (assert (> (count ks) 1))
  (update-in m (drop-last ks) dissoc (last ks)))

(defn safe-merge [m1 m2]
  {:pre [(not-any? #(contains? m1 %) (keys m2))]}
  (merge m1 m2))
