(ns moon.entity.state)

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

(defmulti exit
  (fn [[k _v] _eid _ctx]
    k))

(defmethod exit :default
  [[_k _v] _eid _ctx]
  nil)

(defmulti cursor
  (fn [[k _v] _eid _ctx]
    k))

(defmulti pause-game?
  (fn [k]
    k))

(defmulti clicked-inventory-cell
  (fn [[k _v] _eid _cell]
    k))

(defmethod clicked-inventory-cell :default
  [_ _eid _cell]
  nil)

(defmulti draw-ui-view
  (fn [[k _v] _eid _ctx]
    k))

(defmethod draw-ui-view :default
  [_ _eid _ctx]
  nil)

(defmulti handle-input
  (fn [[k _v] _eid _ctx]
    k))

(defmethod handle-input :default
  [_ _eid _ctx]
  nil)
