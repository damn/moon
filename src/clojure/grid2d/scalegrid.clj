(ns clojure.grid2d.scalegrid
  (:require [moon.grid2d :as g2d]))

(defn f [grid factor]
  (g2d/create-grid (* (g2d/width grid) factor)
                   (* (g2d/height grid) factor)
                   (fn [posi]
                     (get grid (mapv #(int (/ % factor)) posi)))))
