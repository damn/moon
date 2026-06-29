(ns moon.create-entity-state)

(defmulti f
  (fn [[k _v] _eid _ctx]
    k))
