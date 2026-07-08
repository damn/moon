(ns clojure.g2d.adjacent-wall-positions
  (:require [clojure.get-8-neighbours :refer [get-8-neighbours]]
            [clojure.posis :as posis]))

(defn f [grid]
  (filter (fn [p] (and (= :wall (get grid p))
                       (some #(= :ground (get grid %))
                             (get-8-neighbours p))))
          (posis/f grid)))
