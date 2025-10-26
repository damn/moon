(ns moon.entity.projectile-collision)

(defn create [v _world]
  (assoc v :already-hit-bodies #{}))
