(ns clojure.g2d.adjacent-wall-positions
  (:require [moon.position :as position]
            [moon.g2d :as g2d]))

(defn f [grid]
  ; extract conditional ? ??
  (filter (fn [p] (and (= :wall (get grid p))
                       (some #(= :ground (get grid %))
                             (position/get-8-neighbours p))))
          (g2d/posis grid)))
