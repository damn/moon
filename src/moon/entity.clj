(ns moon.entity)

(defmulti create
  (fn [[k _v] _world]
    k))

(defmethod create :default
  [[_ v] _world]
  v)

(defmulti after-create
  (fn [[k _v] _eid _world]
    k))

(defmethod after-create :default
  [[_k _v] _eid _world]
  nil)

(defmulti destroy
  (fn [[k _v] eid]
    k))

(defmethod destroy :default
  [[_k _v] _eid]
  nil)

; tick

; render ?
