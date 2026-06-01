(ns clojure.grid2d.scale-grid
  (:require [clojure.grid2d :as g2d]))

(defn f [grid [w h]]
  (g2d/create-grid (* (g2d/width grid)  w)
                   (* (g2d/height grid) h)
                   (fn [[x y]]
                     (get grid
                          [(int (/ x w))
                           (int (/ y h))]))))
