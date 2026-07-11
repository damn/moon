(ns clojure.scale-grid
  (:require [moon.g2d :as g2d]))

(defn f [grid [w h]]
  (g2d/create (* (g2d/width grid)  w)
              (* (g2d/height grid) h)
              (fn [[x y]]
                (get grid
                     [(int (/ x w))
                      (int (/ y h))]))))
