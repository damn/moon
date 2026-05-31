(ns moon.grid2d)
; 2dimvector is 7x faster than a hashmap of [x y] to values
; like in rich hickey ant demo vectors of vectors:
; https://github.com/juliangamble/clojure-ants-simulation/blob/master/src/ants.clj

(defprotocol Grid2D
  (posis [this])
  (cells [this])
  (width [this])
  (height [this]))

(deftype VectorGrid [data]
  Grid2D
  (posis [this]
    (for [x (range (width this))
          y (range (height this))]
      [x y]))
  (cells [this] (apply concat data))
  (width [this] (count data))
  (height [this] (count (data 0)))

  clojure.lang.ILookup
  (valAt [this p]  ; {x 0 y 1} or [x y] is much slower
    (-> data
        (nth (nth p 0) nil)
        (nth (nth p 1) nil)))

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

(defn- vector2d [w h f]
  (mapv (fn [x] (mapv (fn [y] (f [x y]))
                      (range h)))
        (range w)))

(defn create-grid
  [w h xyfn]
  {:pre [(>= w 1) (>= h 1)]}
  (VectorGrid. (vector2d w h xyfn)))

(defn print-grid [grid & {print-cell :print-cell
                          :or {print-cell
                               #(print (case % :wall "#" :ground "_" "?"))}}]
  (doseq [y (range (height grid))]
    (doseq [x (range (width grid))]
      (print-cell (grid [x y])))
    (println)))

(defn mapgrid->vectorgrid
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
    [(create-grid (+ width 2) (+ height 2)
                  (fn [[x y]]
                    ; new grid starts 1 left/top of leftest cell
                    (calc-newgrid-value (get grid [(+ x min-x -1)
                                                   (+ y min-y -1)]))))
     convert]))

(defn assoc-ks [m ks v]
  (if (empty? ks)
    m
    (apply assoc m (interleave ks (repeat v)))))

(defn get-cells [g2d int-positions]
  (into [] (keep g2d) int-positions))

(defn scale-grid [grid [w h]]
  (create-grid (* (width grid)  w)
               (* (height grid) h)
               (fn [[x y]]
                 (get grid
                      [(int (/ x w))
                       (int (/ y h))]))))

(defn scalegrid [grid factor]
  (create-grid (* (width grid) factor)
               (* (height grid) factor)
               (fn [posi]
                 (get grid (mapv #(int (/ % factor)) posi)))))
