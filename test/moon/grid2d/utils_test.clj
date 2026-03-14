(ns moon.grid2d.utils-test
  (:require [moon.caves :as caves]
            [moon.grid2d :as g2d]))

(comment
 (let [{:keys [start grid]} (caves/create (java.util.Random.) 15 15 :wide)
       _ (println "BASE GRID:\n")
       _ (g2d/printgrid grid)
       ;_ (println)
       ;_ (println "WITH START POSITION (0) :\n")
       ;_ (printgrid (assoc grid start 0))
       ;_ (println "\nwidth:  " (g2d/width  grid)
       ;           "height: " (g2d/height grid)
       ;           "start " start "\n")
       ;_ (println (g2d/posis grid))
       _ (println "\n\n")
       filled (g2d/flood-fill grid start (fn [p] (= :ground (get grid p))))
       _ (g2d/printgrid (reduce #(assoc %1 %2 nil) grid filled))])
 )
