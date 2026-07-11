(ns clojure.scalegrid
  (:require [clojure.grid2d :as g2d]
            [moon.g2d :as moon-g2d]))

(defn f [grid factor]
  (g2d/create-grid (* (moon-g2d/width grid) factor)
                   (* (moon-g2d/height grid) factor)
                   (fn [posi]
                     (get grid (mapv #(int (/ % factor)) posi)))))
