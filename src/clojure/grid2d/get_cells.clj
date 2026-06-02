(ns clojure.grid2d.get-cells)

(defn get-cells [g2d int-positions]
  (into [] (keep g2d) int-positions))
