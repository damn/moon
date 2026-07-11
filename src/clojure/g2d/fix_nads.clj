(ns clojure.g2d.fix-nads
  (:require [moon.m :as m]
            [clojure.g2d.get-nads :refer [get-nads]]
            [moon.g2d :as g2d]
            [clojure.g2d.get-tiles-needing-fix-for-nad :as get-tiles-needing-fix-for-nad]))

(defn f [grid]
  {:pre [(= #{:wall :ground} (set (g2d/cells grid)))]
   :post [(= #{:wall :ground} (set (g2d/cells %)))]}
  (m/assoc-ks grid
              (mapcat #(get-tiles-needing-fix-for-nad/f grid %)
                      (get-nads grid))
              :ground))
