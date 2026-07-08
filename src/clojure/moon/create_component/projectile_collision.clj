(ns clojure.moon.create-component.projectile-collision)

(defn f
  [v _ctx]
  (assoc v :already-hit-bodies #{}))
