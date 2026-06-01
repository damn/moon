(ns game.state)

(defmulti create
  (fn [[k _v] _eid _ctx]
    k))

(defmethod create :default
  [[_k v] _eid _ctx]
  v)

(defmulti enter
  (fn [[k _v] _eid]
    k))

(defmethod enter :default
  [[_k _v] _eid]
  nil)

(defmulti clicked-inventory-cell
  (fn [[k _v] _eid _cell]
    k))

(defmulti draw-ui-view
  (fn [[k _v] _eid _ctx]
    k))

(defmethod draw-ui-view :default
  [_ _eid _ctx]
  nil)
