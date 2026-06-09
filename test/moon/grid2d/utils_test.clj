(ns moon.grid2d.utils-test
  (:require [moon.caves :as caves]
            [clojure.grid2d :as g2d]
            [clojure.grid2d.printgrid :as printgrid]
            [clojure.grid2d.flood-fill :as flood-fill]))

(comment
 (let [{:keys [start grid]} (caves/create (java.util.Random.) 15 15 :wide)
       _ (println "BASE GRID:\n")
       _ (printgrid/f grid)
       ;_ (println)
       ;_ (println "WITH START POSITION (0) :\n")
       ;_ (printgrid/f (assoc grid start 0))
       ;_ (println "\nwidth:  " (g2d/width  grid)
       ;           "height: " (g2d/height grid)
       ;           "start " start "\n")
       ;_ (println (posis/f grid))
       _ (println "\n\n")
       filled (flood-fill/f grid start (fn [p] (= :ground (get grid p))))
       _ (printgrid/f (reduce #(assoc %1 %2 nil) grid filled))])
 )
