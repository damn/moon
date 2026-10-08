(ns g2d.flood-fill
  (:require [moon.m :as m]
            [moon.position :as position]))

(defn flood-fill [grid start walk-on-position?]
  (loop [next-positions [start]
         filled []
         grid grid]
    (if (seq next-positions)
      (recur (filter #(and (get grid %)
                           (walk-on-position? %))
                     (distinct
                      (mapcat position/get-8-neighbours
                              next-positions)))
             (concat filled next-positions)
             (m/assoc-ks grid next-positions nil))
      filled)))
