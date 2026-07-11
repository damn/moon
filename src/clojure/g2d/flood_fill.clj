(ns clojure.g2d.flood-fill
  (:require [moon.position :as position]
            [moon.m :as m]))

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
             (m/assoc-ks grid next-positions nil))
      filled)))
