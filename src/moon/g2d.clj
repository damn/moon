(ns moon.g2d)

(defprotocol G2d
  (height [_])
  (width [_])
  (cells [_])
  (posis [_]))

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

(defn scale-uniform [grid factor]
  (create (* (width grid) factor)
          (* (height grid) factor)
          (fn [posi]
            (get grid (mapv #(int (/ % factor)) posi)))))

(defn scale-by [grid [w h]]
  (create (* (width grid) w)
          (* (height grid) h)
          (fn [[x y]]
            (get grid
                 [(int (/ x w))
                  (int (/ y h))]))))
