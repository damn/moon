(ns clojure.fix-nads
  (:require [clojure.m.assoc-ks :refer [assoc-ks]]
            [clojure.get-nads :refer [get-nads]]
            [clojure.g2d.cells :refer [->cells]]
            [clojure.get-tiles-needing-fix-for-nad :as get-tiles-needing-fix-for-nad]))

(defn f [grid]
  {:pre [(= #{:wall :ground} (set (->cells grid)))]
   :post [(= #{:wall :ground} (set (->cells %)))]}
  (assoc-ks grid
            (mapcat #(get-tiles-needing-fix-for-nad/f grid %)
                    (get-nads grid))
            :ground))
