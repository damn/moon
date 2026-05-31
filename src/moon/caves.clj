(ns moon.caves
  "Cave Algorithmus.
  http://properundead.com/2009/03/cave-generator.html
  http://properundead.com/2009/07/procedural-generation-3-cave-source.html
  http://forums.tigsource.com/index.php?topic=5174.0"
  (:require [clojure.m.assoc-ks :refer [assoc-ks]]
            [clojure.grid2d.mapgrid-to-vectorgrid :as mapgrid->vectorgrid]
            [moon.caves.helpers :as h]))

; gute ergebnisse: :wide / 500-4000 max-cells / turn-ratio 0.5
; besser 150x150 anstatt 100x100 w h
; TODO glaubich einziger unterschied noch: openpaths wird bei jeder cell neu berechnet?
; TODO max-tries wenn er nie �ber min-cells kommt? -> im let dazu definieren vlt max 30 sekunden -> in tries umgerechnet??
(defn create [random min-cells max-cells adjnum-type]
  ; move up where its used only
  (reset! h/current-order (h/create-order random))
  (let [start [0 0]
        start-grid (assoc {} start :ground) ; grid of posis to :ground or no entry for walls
        finished (fn [grid end cell-cnt]
                   ;(println "Reached cells: " cell-cnt) ; TODO cell-cnt stimmt net genau
                   ; TODO already called there down ... make mincells check there
                   (if (< cell-cnt min-cells)
                     (create random min-cells max-cells adjnum-type) ; recur?
                     (let [[grid convert] (mapgrid->vectorgrid/f grid #(if (nil? %) :wall :ground))]
                       {:grid  grid
                        :start (convert start)
                        :end   (convert end)})))]
    (loop [posi-seq [start]
           grid     start-grid
           cell-cnt 0]
      ; TODO min cells check !?
      (if (>= cell-cnt max-cells)
        (finished grid
                  (last posi-seq)
                  cell-cnt)
        (let [try-carve-posis (h/create-rand-4-neighbour-posis
                               (last posi-seq) ; TODO take random ! at corner ... hmm
                               ((h/get-adj-num adjnum-type) (count posi-seq) random)
                               random)
              carve-posis (filter #(nil? (get grid %)) try-carve-posis)
              new-pos-seq (concat (drop-last posi-seq) carve-posis)]
          (if (not-empty new-pos-seq)
            (recur new-pos-seq
                   (if (seq carve-posis)
                     (assoc-ks grid carve-posis :ground)
                     grid)
                   (+ cell-cnt (count carve-posis)))
            ; TODO here min-cells check ?
            (finished grid (last posi-seq) cell-cnt)))))))
