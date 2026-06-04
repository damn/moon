(ns clojure.grid2d.adjacent-wall-positions
  (:require [clojure.math.position.get-8-neighbours :refer [get-8-neighbours]]
            [clojure.grid2d :as g2d]))

(defn f [grid]
  (filter (fn [p] (and (= :wall (get grid p))
                       (some #(= :ground (get grid %))
                             (get-8-neighbours p))))
          (g2d/posis grid)))
