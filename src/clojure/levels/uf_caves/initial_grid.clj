(ns clojure.levels.uf-caves.initial-grid
  (:require [moon.g2d :as g2d]))

(defn f
  [{:keys [initial-grid-create-fn
           size
           cave-style
           random]
    :as level}]
  (let [{:keys [start grid]} (initial-grid-create-fn random size size cave-style)]
    (assert (= #{:wall :ground} (set (g2d/cells grid))))
    (assoc level
           :level/start start
           :level/grid grid)))
