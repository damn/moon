(ns game.entity)

(defmulti render
  (fn [[k _v] _entity _ctx]
    k))
