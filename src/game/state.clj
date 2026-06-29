(ns game.state)

(defmulti create
  (fn [[k _v] _eid _ctx]
    k))

(defmulti draw-ui-view
  (fn [[k _v] _eid _ctx]
    k))
