(ns moon.ops.add)

(defn f [ops other-ops]
  (merge-with + ops other-ops))
