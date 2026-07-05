(ns moon.caves
  "Cave Algorithmus.
  http://properundead.com/2009/03/cave-generator.html
  http://properundead.com/2009/07/procedural-generation-3-cave-source.html
  http://forums.tigsource.com/index.php?topic=5174.0"
  (:require [grid2d.mapgrid-to-vectorgrid :as mapgrid->vectorgrid]
            [position.get-4-neighbours :refer [get-4-neighbours]]
            [map.assoc-ks :refer [assoc-ks]]
            [rand.sshuffle :refer [sshuffle]]
            [rand.srand :refer [srand]]
            [rand.srand-int :refer [srand-int]]))

(defn- create-order [random]
  (sshuffle (range 4) random))

(defn- get-in-order [v order]
  (map #(get v %) order))

(def ^:private turn-ratio 0.25)

(defn- get-default-adj-num [open-paths random]
  (if (= open-paths 1)
    (case (int (srand-int 4 random))
      0 1
      1 1
      2 1
      3 2
      1)
    (case (int (srand-int 4 random))
      0 0
      1 1
      2 1
      3 2
      1)))

(defn- get-thin-adj-num [open-paths random]
  (if (= open-paths 1)
    1
    (case (int (srand-int 7 random))
      0 0
      1 2
      1)))

(defn- get-wide-adj-num [open-paths random]
  (if (= open-paths 1)
    (case (int (srand-int 3 random))
      0 1
      2)
    (case (int (srand-int 4 random))
      0 1
      1 2
      2 3
      3 4
      1)))

(def ^:private get-adj-num
  {:wide    get-wide-adj-num
   :thin    get-thin-adj-num    ; h�hle mit breite 1 �berall nur -> turn-ratio verringern besser
   :default get-default-adj-num}) ; etwas breiter als 1 aber immernoch zu d�nn f�r m ein game -> turn-ratio verringern besser

; gute ergebnisse: :wide / 500-4000 max-cells / turn-ratio 0.5
; besser 150x150 anstatt 100x100 w h
; TODO glaubich einziger unterschied noch: openpaths wird bei jeder cell neu berechnet?
; TODO max-tries wenn er nie �ber min-cells kommt? -> im let dazu definieren vlt max 30 sekunden -> in tries umgerechnet??
(defn create [random min-cells max-cells adjnum-type]
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
           cell-cnt 0
           current-order (create-order random)]
      ; TODO min cells check !?
      (if (>= cell-cnt max-cells)
        (finished grid
                  (last posi-seq)
                  cell-cnt)
        (let [current-order (if (< (srand random) turn-ratio)
                              (create-order random)
                              current-order)
              try-carve-posis (take ((get-adj-num adjnum-type) (count posi-seq) random)
                                    (get-in-order (get-4-neighbours (last posi-seq))
                                                  current-order))
              carve-posis (filter #(nil? (get grid %)) try-carve-posis)
              new-pos-seq (concat (drop-last posi-seq) carve-posis)]
          (if (not-empty new-pos-seq)
            (recur new-pos-seq
                   (if (seq carve-posis)
                     (assoc-ks grid carve-posis :ground)
                     grid)
                   (+ cell-cnt (count carve-posis))
                   current-order)
            ; TODO here min-cells check ?
            (finished grid (last posi-seq) cell-cnt)))))))
