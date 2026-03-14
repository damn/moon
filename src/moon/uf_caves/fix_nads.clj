(ns moon.uf-caves.fix-nads
  (:require [moon.grid2d :as g2d]
            [moon.nads :as nads]))

(defn step
  [{:keys [level/grid]
    :as level}]
  (assert (= #{:wall :ground} (set (g2d/cells grid))))
  (let [grid (nads/fix-nads grid)]
    (assert (= #{:wall :ground} (set (g2d/cells grid))))
    (assoc level :level/grid grid)))
