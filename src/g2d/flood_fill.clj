(ns g2d.flood-fill
  (:require [moon.g2d :as g2d]
            [moon.position :as position]))

(defn flood-fill [grid start walk-on-position?]
  (loop [next-positions [start]
         filled []
         grid grid]
    (if (seq next-positions)
      (recur (filter #(and (g2d/get grid %)
                           (walk-on-position? %))
                     (distinct
                      (mapcat position/get-8-neighbours
                              next-positions)))
             (concat filled next-positions)
             (reduce #(g2d/assoc %1 %2 nil) grid next-positions))
      filled)))
