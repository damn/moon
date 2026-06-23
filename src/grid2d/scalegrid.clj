(ns grid2d.scalegrid
  (:require [clojure.grid2d :as g2d]
            [grid2d.height :refer [->height]]
            [grid2d.width :refer [->width]]))

(defn f [grid factor]
  (g2d/create-grid (* (->width grid) factor)
                   (* (->height grid) factor)
                   (fn [posi]
                     (get grid (mapv #(int (/ % factor)) posi)))))
