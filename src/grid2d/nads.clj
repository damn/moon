(ns grid2d.nads
  (:require [map.assoc-ks :refer [assoc-ks]]
            [grid2d.get-nads :refer [get-nads]]
            [grid2d.cells :refer [->cells]]
            [grid2d.is-nad-corner :as nad-corner?]))

(defn- get-tiles-needing-fix-for-nad [grid [[fromx fromy] [tox toy]]]
  (let [xstep (- tox fromx)
        ystep (- toy fromy)
        cell1x (+ fromx xstep)
        cell1y fromy
        cell1 [cell1x cell1y]
        cell11 [(+ cell1x xstep) (+ cell1y (- ystep))]
        cell2x (+ cell1x xstep)
        cell2y cell1y
        cell2 [cell2x cell2y]
        cell21 [(+ cell2x xstep) (+ cell2y ystep)]
        cell3 [cell2x (+ cell2y ystep)]]
;    (println "from: " [fromx fromy] " to: " [tox toy])
;    (println "xstep " xstep " ystep " ystep)
;    (println "cell1 " cell1)
;    (println "cell11 " cell11)
;    (println "cell2 " cell2)
;    (println "cell21 " cell21)
;    (println "cell3 " cell3)
    (if-not (nad-corner?/f grid cell1 cell11)
      [cell1]
      (if-not (nad-corner?/f grid cell2 cell21)
        [cell1 cell2]
        [cell1 cell2 cell3]))))

(defn- mark-nads [grid nads label]
  (assoc-ks grid (mapcat #(get-tiles-needing-fix-for-nad grid %) nads) label))

(defn fix-nads [grid]
  {:pre [(= #{:wall :ground} (set (->cells grid)))]
   :post [(= #{:wall :ground} (set (->cells %)))]}
  (mark-nads grid (get-nads grid) :ground))
