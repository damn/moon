(ns clojure.g2d.fix-nads
  (:require [moon.m :as m]
            [moon.g2d :as g2d]
            [clojure.g2d.get-tiles-needing-fix-for-nad :as get-tiles-needing-fix-for-nad]))

(defn f [grid]
  {:pre [(= #{:wall :ground} (set (g2d/cells grid)))]
   :post [(= #{:wall :ground} (set (g2d/cells %)))]}
  (m/assoc-ks grid
              (mapcat #(get-tiles-needing-fix-for-nad/f grid %)
                      (g2d/get-nads grid))
              :ground))
