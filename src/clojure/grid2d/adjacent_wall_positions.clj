(ns clojure.grid2d.adjacent-wall-positions
  (:require [clojure.position.get-8-neighbours :refer [get-8-neighbours]]
            [clojure.grid2d.posis :as posis]))

(defn f [grid]
  (filter (fn [p] (and (= :wall (get grid p))
                       (some #(= :ground (get grid %))
                             (get-8-neighbours p))))
          (posis/f grid)))
