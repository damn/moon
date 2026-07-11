(ns moon.m)

(defn assoc-ks [m ks v]
  (if (empty? ks)
    m
    (apply assoc m (interleave ks (repeat v)))))

(defn dissoc-in [m ks]
  (assert (> (count ks) 1))
  (update-in m (drop-last ks) dissoc (last ks)))

(defn safe-merge [m1 m2]
  {:pre [(not-any? #(contains? m1 %) (keys m2))]}
  (merge m1 m2))
