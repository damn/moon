(ns moon.entity)

(defmulti create
  (fn [[k _v] _world]
    k))

(defmethod create :default
  [[_ v] _world]
  v)
