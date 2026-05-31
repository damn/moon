(ns clojure.grid2d.flood-fill
  (:require [moon.position :as position]
            [moon.grid2d :as g2d]))

(defn f [grid start walk-on-position?]
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
             (g2d/assoc-ks grid next-positions nil))
      filled)))
