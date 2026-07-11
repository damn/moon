(ns clojure.scale-grid
  (:require [clojure.grid2d :as g2d]
            [moon.g2d :as moon-g2d]
            [clojure.g2d.width :refer [->width]]))

(defn f [grid [w h]]
  (g2d/create-grid (* (->width grid)  w)
                   (* (moon-g2d/height grid) h)
                   (fn [[x y]]
                     (get grid
                          [(int (/ x w))
                           (int (/ y h))]))))
