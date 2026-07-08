(ns clojure.ops.remove)

(defn f [ops other-ops]
  (merge-with - ops other-ops))
