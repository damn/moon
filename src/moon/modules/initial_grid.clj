(ns moon.modules.initial-grid
  (:require [clojure.grid2d :as g2d]
            [moon.caves :as caves]
            [moon.nads :as nads])
  (:import (java.util Random)))

(defn- cave-grid [& {:keys [size]}]
  (let [{:keys [start grid]} (caves/create (Random.) size size :wide)
        grid (nads/fix-nads grid)]
    (assert (= #{:wall :ground} (set (g2d/cells grid))))
    {:start start
     :grid grid}))

(defn step
  [{:keys [world/map-size]
    :as world-fn-ctx}]
  (let [{:keys [start grid]} (cave-grid :size map-size)]
    (assoc world-fn-ctx
           :start start
           :grid grid)))
