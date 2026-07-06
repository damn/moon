(ns clojure.grid2d.fix-nads
  (:require [clojure.map.assoc-ks :refer [assoc-ks]]
            [clojure.grid2d.get-nads :refer [get-nads]]
            [clojure.grid2d.cells :refer [->cells]]
            [clojure.grid2d.get-tiles-needing-fix-for-nad :as get-tiles-needing-fix-for-nad]))

(defn f [grid]
  {:pre [(= #{:wall :ground} (set (->cells grid)))]
   :post [(= #{:wall :ground} (set (->cells %)))]}
  (assoc-ks grid
            (mapcat #(get-tiles-needing-fix-for-nad/f grid %)
                    (get-nads grid))
            :ground))
