(ns clojure.g2d.flood-fill
  (:require [clojure.get-8-neighbours :refer [get-8-neighbours]]
            [clojure.assoc-ks :refer [assoc-ks]]))

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
