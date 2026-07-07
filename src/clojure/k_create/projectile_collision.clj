(ns clojure.k-create.projectile-collision)

(defn f
  [v _ctx]
  (assoc v :already-hit-bodies #{}))
