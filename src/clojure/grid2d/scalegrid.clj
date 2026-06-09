(ns clojure.grid2d.scalegrid
  (:require [clojure.grid2d :as g2d]
            [clojure.grid2d.height :refer [->height]]
            [clojure.grid2d.width :refer [->width]]))

(defn f [grid factor]
  (g2d/create-grid (* (->width grid) factor)
                   (* (->height grid) factor)
                   (fn [posi]
                     (get grid (mapv #(int (/ % factor)) posi)))))
