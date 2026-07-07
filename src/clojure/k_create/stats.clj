(ns clojure.k-create.stats)

(defn f
  [v _ctx]
  (-> v
      (update :stats/mana (fn [v] [v v]))
      (update :stats/hp   (fn [v] [v v]))))
