(ns moon.uf-caves.initial-grid
  (:require [moon.caves :as caves]
            [moon.grid2d :as g2d]))

(defn step
  [{:keys [size
           cave-style
           random]
    :as level}]
  (let [{:keys [start grid]} (caves/create random size size cave-style)]
    (assert (= #{:wall :ground} (set (g2d/cells grid))))
    (assoc level
           :level/start start
           :level/grid grid)))
