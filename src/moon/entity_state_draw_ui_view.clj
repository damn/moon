(ns moon.entity-state-draw-ui-view)

(defmulti f
  (fn [[k _v] _eid _ctx]
    k))
