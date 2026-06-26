(ns moon.grid2d.utils-test
  (:require [moon.caves :as caves]
            [grid2d.height :refer [->height]]
            [grid2d.width :refer [->width]]
            [grid2d.printgrid :as printgrid]
            [grid2d.flood-fill :as flood-fill]))

(comment
 (let [{:keys [start grid]} (caves/create (clojure.java.util.random/create) 15 15 :wide)
       _ (println "BASE GRID:\n")
       _ (printgrid/f grid)
       ;_ (println)
       ;_ (println "WITH START POSITION (0) :\n")
       ;_ (printgrid/f (assoc grid start 0))
       ;_ (println "\nwidth:  " (->width  grid)
       ;           "height: " (->height grid)
       ;           "start " start "\n")
       ;_ (println (posis/f grid))
       _ (println "\n\n")
       filled (flood-fill/f grid start (fn [p] (= :ground (get grid p))))
       _ (printgrid/f (reduce #(assoc %1 %2 nil) grid filled))])
 )
