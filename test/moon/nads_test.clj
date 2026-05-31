(ns moon.nads-test)

(comment
 (def found (atom false))

 (defn search-buggy-nads []
   (println "searching buggy nads")
   (doseq [n (range 100000)
           :when (not @found)]
     (println "try " n)
     (let [grid (cellular-automata-gridgen 100 80 :fillprob 62 :generations 0 :wall-borders true)
           nads (get-nads grid)
           fixed-grid (mark-nads grid nads :ground)]
       (when
         (and
          (not (zero? (count nads)))
          (not (zero? (count (get-nads fixed-grid)))))
         (println "found!")
         (reset! found [grid fixed-grid]))))
   (println "found buggy nads? " @found))

 )
