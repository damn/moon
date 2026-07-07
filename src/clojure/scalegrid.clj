(ns clojure.scalegrid
  (:require [clojure.grid2d :as g2d]
            [clojure.height :refer [->height]]
            [clojure.width :refer [->width]]))

(defn f [grid factor]
  (g2d/create-grid (* (->width grid) factor)
                   (* (->height grid) factor)
                   (fn [posi]
                     (get grid (mapv #(int (/ % factor)) posi)))))
