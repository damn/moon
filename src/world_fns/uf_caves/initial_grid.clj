(ns world-fns.uf-caves.initial-grid
  (:require [clojure.grid2d.cells :refer [->cells]]))

(defn f
  [{:keys [initial-grid-create-fn
           size
           cave-style
           random]
    :as level}]
  (let [{:keys [start grid]} (initial-grid-create-fn random size size cave-style)]
    (assert (= #{:wall :ground} (set (->cells grid))))
    (assoc level
           :level/start start
           :level/grid grid)))
