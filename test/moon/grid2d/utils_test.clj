(ns moon.grid2d.utils-test
  (:require [moon.caves :as caves]
            [moon.g2d :as g2d]
            [clojure.g2d.flood-fill :as flood-fill])
  (:import (java.util Random)))

(comment
 (let [{:keys [start grid]} (caves/create (Random.) 15 15 :wide)
       _ (println "BASE GRID:\n")
       _ (g2d/print-y-up grid)
       ;_ (println)
       ;_ (println "WITH START POSITION (0) :\n")
       ;_ (g2d/print-y-up (assoc grid start 0))
       ;_ (println "\nwidth:  " (g2d/width  grid)
       ;           "height: " (g2d/height grid)
       ;           "start " start "\n")
       ;_ (println (g2d/posis grid))
       _ (println "\n\n")
       filled (flood-fill/f grid start (fn [p] (= :ground (get grid p))))
       _ (g2d/print-y-up (reduce #(assoc %1 %2 nil) grid filled))])
 )
