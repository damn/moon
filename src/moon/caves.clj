(ns moon.caves
  "Cave Algorithmus.
  http://properundead.com/2009/03/cave-generator.html
  http://properundead.com/2009/07/procedural-generation-3-cave-source.html
  http://forums.tigsource.com/index.php?topic=5174.0"
  (:require [moon.g2d :as g2d]
            [moon.position :as position])
  (:import (java.util Random)))

(defn- sshuffle
  "Return a random permutation of coll"
  [coll random]
  (let [al (java.util.ArrayList. ^java.util.Collection coll)]
    (java.util.Collections/shuffle al random)
    (clojure.lang.RT/vector (.toArray al))))

(defn- from-mapgrid
  "Transforms a grid of {position value} to a grid2d.
  Returns [grid convert-fn]: convert-fn converts a position of the old grid to a position of the new one."
  [grid calc-newgrid-value]
  (let [posis (keys grid)
        xs (map #(% 0) posis)
        min-x (apply min xs)
        max-x (apply max xs)
        ys (map #(% 1) posis)
        min-y (apply min ys)
        max-y (apply max ys)
        width (inc (- max-x min-x))
        height (inc (- max-y min-y))
        convert (fn [[x y]] [(- x min-x -1)
                             (- y min-y -1)])]
    ; +2 so there are walls on all borders around the farthest ground cells
    [(g2d/create (+ width 2) (+ height 2)
                 (fn [[x y]]
                   ; new grid starts 1 left/top of leftest cell
                   (calc-newgrid-value (get grid [(+ x min-x -1)
                                                  (+ y min-y -1)]))))
     convert]))

; gute ergebnisse: :wide / 500-4000 max-cells / turn-ratio 0.5
; besser 150x150 anstatt 100x100 w h
; TODO glaubich einziger unterschied noch: openpaths wird bei jeder cell neu berechnet?
; TODO max-tries wenn er nie über min-cells kommt? -> im let dazu definieren vlt max 30 sekunden -> in tries umgerechnet??
(defn generate [random min-cells max-cells get-adj-num-fn]
  (let [create-order (fn [] (sshuffle (range 4) random))
        turn-ratio 0.25
        start [0 0]
        start-grid (assoc {} start :ground) ; grid of posis to :ground or no entry for walls
        finished (fn [grid end cell-cnt]
                   ;(println "Reached cells: " cell-cnt) ; TODO cell-cnt stimmt net genau
                   ; TODO already called there down ... make mincells check there
                   (if (< cell-cnt min-cells)
                     (generate random min-cells max-cells get-adj-num-fn) ; recur?
                     (let [[grid convert] (from-mapgrid grid #(if (nil? %) :wall :ground))]
                       {:grid  grid
                        :start (convert start)
                        :end   (convert end)})))]
    (loop [posi-seq [start]
           grid     start-grid
           cell-cnt 0
           current-order (create-order)]
      ; TODO min cells check !?
      (if (>= cell-cnt max-cells)
        (finished grid
                  (last posi-seq)
                  cell-cnt)
        (let [current-order (if (< (.nextFloat ^Random random) turn-ratio)
                              (create-order)
                              current-order)
              neighbours (position/get-4-neighbours (last posi-seq))
              try-carve-posis (take (get-adj-num-fn (count posi-seq) random)
                                    (map #(get neighbours %) current-order))
              carve-posis (filter #(nil? (get grid %)) try-carve-posis)
              new-pos-seq (concat (drop-last posi-seq) carve-posis)]
          (if (not-empty new-pos-seq)
            (recur new-pos-seq
                   (reduce #(assoc %1 %2 :ground) grid carve-posis)
                   (+ cell-cnt (count carve-posis))
                   current-order)
            ; TODO here min-cells check ?
            (finished grid (last posi-seq) cell-cnt)))))))

(def k->adj-num
  {:wide
   (fn [open-paths random]
     (if (= open-paths 1)
       (case (int (* 3 (.nextFloat ^Random random)))
         0 1
         2)
       (case (int (* 4 (.nextFloat ^Random random)))
         0 1
         1 2
         2 3
         3 4
         1)))

   :thin
   ; höhle mit breite 1 überall nur -> turn-ratio verringern besser
   (fn [open-paths random]
     (if (= open-paths 1)
       1
       (case (int (* 7 (.nextFloat ^Random random)))
         0 0
         1 2
         1)))

   :default
   ; etwas breiter als 1 aber immernoch zu dünn für m ein game -> turn-ratio verringern besser
   (fn [open-paths random]
     (if (= open-paths 1)
       (case (int (* 4 (.nextFloat ^Random random)))
         0 1
         1 1
         2 1
         3 2
         1)
       (case (int (* 4 (.nextFloat ^Random random)))
         0 0
         1 1
         2 1
         3 2
         1)))})

(defn create [random min-cells max-cells adjnum-type]
  (generate random min-cells max-cells (k->adj-num adjnum-type)))
