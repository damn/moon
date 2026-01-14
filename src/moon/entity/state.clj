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
