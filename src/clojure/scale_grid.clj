(ns clojure.scale-grid
  (:require [clojure.grid2d :as g2d]
            [clojure.height :refer [->height]]
            [clojure.width :refer [->width]]))

(defn f [grid [w h]]
  (g2d/create-grid (* (->width grid)  w)
                   (* (->height grid) h)
                   (fn [[x y]]
                     (get grid
                          [(int (/ x w))
                           (int (/ y h))]))))
