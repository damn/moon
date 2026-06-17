(ns clojure.grid2d.flood-fill
  (:require [clojure.math.position.get-8-neighbours :refer [get-8-neighbours]]
            [clojure.m.assoc-ks :refer [assoc-ks]]))

(defn f [grid start walk-on-position?]
  (loop [next-positions [start]
         filled []
         grid grid]
    (if (seq next-positions)
      (recur (filter #(and (get grid %)
                           (walk-on-position? %))
                     (distinct
                      (mapcat get-8-neighbours
                              next-positions)))
             (concat filled next-positions)
             (assoc-ks grid next-positions nil))
      filled)))
