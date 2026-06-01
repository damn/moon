(ns game.entity)

; TODO:
; no [k v] first arg, just v !
#_(defn tick [[k v] eid ctx]
  ((get tick-fns k) v eid ctx))

(defmulti tick
  (fn [[k _v] _eid _ctx]
    k))

(defmethod tick :default
  [[_k _v] _eid _ctx]
  nil)

(defmulti render
  (fn [[k _v] _entity _ctx]
    k))
