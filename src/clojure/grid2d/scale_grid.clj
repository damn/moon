(ns clojure.grid2d.scale-grid
  (:require [clojure.grid2d :as g2d]
            [clojure.grid2d.height :refer [->height]]
            [clojure.grid2d.width :refer [->width]]))

(defn f [grid [w h]]
  (g2d/create-grid (* (->width grid)  w)
                   (* (->height grid) h)
                   (fn [[x y]]
                     (get grid
                          [(int (/ x w))
                           (int (/ y h))]))))
