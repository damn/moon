(ns moon.g2d
  (:require [moon.m :as m]
            [moon.position :as position]))

(defprotocol G2d
  (height [_])
  (width [_])
  (cells [_])
  (posis [_]))

(defn adjacent-wall-positions [g2d]
  (filter (fn [position]
            (and (= :wall (get g2d position))
                 (some #(= :ground (get g2d %))
                       (position/get-8-neighbours position))))
          (posis g2d)))

; can adjust:
; * split percentage , for higher level areas may scale faster (need to be more careful)
; * not 4 neighbors but just 1 tile randomwalk -> possible to have lvl 9 area next to lvl 1 ?
; * adds metagame to the game , avoid/or fight higher level areas, which areas to go next , etc...
; -> up to the player not step by step level increase like D2
; can not only take first of added-p but multiples also
; can make parameter how fast it scales
; area-level-grid works better with more wide grids
; if the cave is very straight then it is just a continous progression and area-level-grid is useless
(defn area-level-grid
  "Expands from start position by adding one random adjacent neighbor.
  Each random walk is a step and is assigned a level as of max-level.
  Levels are scaled, for example grid has 100 ground cells, so steps would be 0 to 100/99?
  and max-level will smooth it out over 0 to max-level.
  The point of this is to randomize the levels so player does not have a smooth progression
  but can encounter higher level areas randomly around but there is always a path which goes from
  level 0 to max-level, so the player has to decide which areas to do in which order."
  [& {:keys [grid start max-level walk-on]}]
  (let [maxcount (->> grid
                      cells
                      (filter walk-on)
                      count)
        ; -> assume all :ground cells can be reached from start
        ; later check steps count == maxcount assert
        level-step (/ maxcount max-level)
        step->level #(int (Math/ceil (/ % level-step)))
        walkable-neighbours (fn [grid position]
                              (filter #(walk-on (get grid %))
                                      (position/get-4-neighbours position)))]
    (loop [next-positions #{start}
           steps          [[0 start]]
           grid           (assoc grid start 0)]
      (let [next-positions (set
                            (filter #(seq (walkable-neighbours grid %))
                                    next-positions))]
        (if (seq next-positions)
          (let [p (rand-nth (seq next-positions))
                added-p (rand-nth (walkable-neighbours grid p))]
            (if added-p
              (let [area-level (step->level (count steps))]
                (recur (conj next-positions added-p)
                       (conj steps [area-level added-p])
                       (assoc grid added-p area-level)))
              (recur next-positions
                     steps
                     grid)))
          {:steps steps
           :area-level-grid grid})))))

(defn assoc-transition-cells [grid]
  (let [grid (reduce #(assoc %1 %2 :transition) grid
                     (adjacent-wall-positions grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (cells grid)))
             (= #{:ground :transition}       (set (cells grid))))
            (str "(set (cells grid)): " (set (cells grid))))
    ;_ (printgrid/f grid)
    ;_ (println)
    grid))

(defn get-cells [g2d int-positions]
  (into [] (keep g2d) int-positions))

(defn print-y-up
  [grid]
  (doseq [y (range (dec (height grid)) -1 -1)]
    (doseq [x (range (width grid))]
      (let [celltype (grid [x y])]
        (print (if (number? celltype)
                 celltype
                 (case celltype
                   nil               "?"
                   :undefined        " "
                   :ground           "_"
                   :wall             "#"
                   :airwalkable      "."
                   :module-placement "X"
                   :start-module     "@"
                   :transition       "+")))))
    (println)))

(defn nad-corner? [grid [fromx fromy] [tox toy]]
  (and
   (= :ground (get grid [tox toy])) ; also filters nil/out of map
   (= :wall (get grid [tox fromy]))
   (= :wall (get grid [fromx toy]))))

; could be made faster because accessing the same posis oftentimes at nad-corner? check
(let [diagonal-steps [[-1 -1] [-1 1] [1 -1] [1 1]]]
  (defn get-nads [grid]
    (loop [checkposis (filter (fn [{y 1 :as posi}]
                                (and (even? y)
                                     (= :ground (get grid posi))))
                              (posis grid))
           result []]
      (if (seq checkposis)
        (let [position (first checkposis)
              diagonal-posis (map #(mapv + position %) diagonal-steps)
              nads (map (fn [nad] [position nad])
                        (filter #(nad-corner? grid position %) diagonal-posis))]
          (recur
           (rest checkposis)
           (doall (concat result nads)))) ; doall else stackoverflow error
        result))))

(defn get-tiles-needing-fix-for-nad [grid [[fromx fromy] [tox toy]]]
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
    (if-not (nad-corner? grid cell1 cell11)
      [cell1]
      (if-not (nad-corner? grid cell2 cell21)
        [cell1 cell2]
        [cell1 cell2 cell3]))))

(defn fix-nads [grid]
  {:pre [(= #{:wall :ground} (set (cells grid)))]
   :post [(= #{:wall :ground} (set (cells %)))]}
  (m/assoc-ks grid
              (mapcat #(get-tiles-needing-fix-for-nad grid %)
                      (get-nads grid))
              :ground))

(defn flood-fill [grid start walk-on-position?]
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

(deftype VectorGrid [data]
  G2d
  (height [_]
    (count (data 0)))

  (width [_]
    (count data))

  (cells [_]
    (apply concat data))

  (posis [this]
    (for [x (range (width this))
          y (range (height this))]
      [x y]))

  clojure.lang.ILookup
  (valAt [this p]
    (-> data
        (nth (p 0) nil)
        (nth (p 1) nil)))

  clojure.lang.IFn
  (invoke [this p] (.valAt this p))

  clojure.lang.Seqable
  (seq [this]
    (map #(vector %1 %2) (posis this) (cells this)))

  clojure.lang.IPersistentCollection
  (equiv [this obj]
    (and (= VectorGrid (class obj))
         (= (.data ^VectorGrid obj) data)))

  clojure.lang.Associative
  (assoc [this p v]
    (VectorGrid. (assoc-in data p v)))
  (containsKey [this [x y]]
    (and (contains? data x)
         (contains? (data 0) y)))

  Object
  (hashCode [this] (.hashCode data))
  (equals [this obj]
    (and (= VectorGrid (class obj))
         (.equals (.data ^VectorGrid obj) data)))
  (toString [this]
    (str "width " (width this) ", height " (height this))))

(defn ->VectorGrid [data]
  (VectorGrid. data))

; 2dimvector is 7x faster than a hashmap of [x y] to values
; like in rich hickey ant demo vectors of vectors:
; https://github.com/juliangamble/clojure-ants-simulation/blob/master/src/ants.clj
(defn create
  [w h xyfn]
  {:pre [(>= w 1) (>= h 1)]}
  (->VectorGrid
   (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                       (range h)))
         (range w))))

(defn from-mapgrid
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
    [(create (+ width 2) (+ height 2)
             (fn [[x y]]
               ; new grid starts 1 left/top of leftest cell
               (calc-newgrid-value (get grid [(+ x min-x -1)
                                              (+ y min-y -1)]))))
     convert]))
