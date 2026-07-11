(ns clojure.g2d.adjacent-wall-positions
  (:require [moon.position :as position]
            [clojure.g2d.posis :as posis]))

(defn f [grid]
  ; extract conditional ? ??
  (filter (fn [p] (and (= :wall (get grid p))
                       (some #(= :ground (get grid %))
                             (position/get-8-neighbours p))))
          (posis/f grid)))
