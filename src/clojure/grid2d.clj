(ns clojure.grid2d)
; 2dimvector is 7x faster than a hashmap of [x y] to values
; like in rich hickey ant demo vectors of vectors:
; https://github.com/juliangamble/clojure-ants-simulation/blob/master/src/ants.clj

(defprotocol Grid2D
  (transform [this f] "f should be a function of [posi old-value] that returns new-value.")
  (posis [this])
  (cells [this])
  (width [this])
  (height [this]))

(defn- transform-values [data width height f]
  (mapv
    (fn [x] (loop [v (transient (get data x))
                   y 0]
              (if (not= y height)
                (recur (assoc! v y (f [x y] (get v y)))
                       (inc y))
                (persistent! v))))
    (range width)))

; TODO could add as fields: posis,cells,seq (maybe delays?)
(deftype VectorGrid [data]

  Grid2D
  (transform [this f]
    (VectorGrid. (transform-values data (width this) (height this) f)))
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
    (VectorGrid. (assoc-in data p v))) ; TODO assoc-in recursion expensive?
  (containsKey [this [x y]]
    (and (contains? data x)
         (contains? (data 0) y)))
  ;(entryAt [this k]) returns IMapEntry, used in find

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
  "Creates a 2 dimensional grid.
  xyfn is a function is applied for every [x y] to get value."
  [w h xyfn]
  {:pre [(>= w 1) (>= h 1)]}
  (VectorGrid. (vector2d w h xyfn)))

;;

(defn print-grid [grid & {print-cell :print-cell
                          :or {print-cell
                               #(print (case % :wall "#" :ground "_" "?"))}}]
  (doseq [y (range (height grid))]
    (doseq [x (range (width grid))]
      (print-cell (grid [x y])))
    (println)))

;;

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

;;

(defn get-4-neighbour-positions [[x y]]
  [[(inc x) y]
   [(dec x) y]
   [x (inc y)]
   [x (dec y)]])

(defn get-8-neighbour-positions [[x y]]
  (for [tx (range (dec x) (+ x 2))
        ty (range (dec y) (+ y 2))
        :when (not= [x y] [tx ty])]
    [tx ty]))

;;

; Could put width and height as deftype arguments instead of protocol functions
(comment (let [g (create-grid 5 5 identity)]
           (compare-times 100000
                          (.width ^VectorGrid g) ; 1.575   ms
                          (.width g)             ; 305.477 ms
                          (width g))))           ; 2.761   ms

; getting multiple cells could be speed up
; if multiple posis in same row => only 1 row access

;Could also use a dogrid or docells function instead of doseq [p cell] -> could be a lot faster
;Time of:  (docells grid myfunc)
;"Elapsed time: 2.455 msecs"
;Time of:  (doseq [[p cell] grid] (myfunc p cell))
;"Elapsed time: 6.649 msecs"
;Time of:  (doseq [cell (cells grid) p (posis grid)] (myfunc p cell))
;"Elapsed time: 442.595 msecs"

; nth is faster than get: and get-in is very slow:
; (let [v (mapv (fn [i] :foo) (range 10000))] (time (dotimes [_ 1e8] (nth v 657))))
; "Elapsed time: 771.518 msecs"
; (let [v (mapv (fn [i] :foo) (range 10000))] (time (dotimes [_ 1e8] (get v 657))))
; "Elapsed time: 1671.101 msecs"
;
; user=> (time (dotimes [_ 1e8] (-> agrid (nth 10) (nth 20))))
; "Elapsed time: 1670.374 msecs"
; user=> (time (dotimes [_ 1e8] (get-in agrid [10 20])))
; "Elapsed time: 15219.404 msecs"
; user=>  (time  (dotimes  [_ 1e8]  (-> agrid  (get 10)  (get 20))))
; "Elapsed time: 2909.678 msecs"
; user=> (time (dotimes [_ 1e8] (.nth ^Indexed (.nth ^Indexed agrid 10) 20)))
; "Elapsed time: 1637.728 msecs"
;
(comment
  (let [data (.data (create-grid 100 100 identity))
        p [23 43]
        n 1e7]
    (time (dotimes [_ n]
            (-> data
                (nth (p 0) nil)
                (nth (p 1) nil))))
    (time (dotimes [_ n]
            (-> data
                (nth (nth p 0) nil)
                (nth (nth p 1) nil))))))
; 250ms
; 230ms

(comment
  (let [g (create-grid 100 100 identity)
        n 1e8]
    (time (dotimes [_ n]     (g [10 13])))
    (time (dotimes [_ n] (get g [10 13])))
    (time (dotimes [_ n] (.valAt ^clojure.lang.ILookup g [10 13])))))
;"Elapsed time: 2440.053 msecs" -> IFn -> .valAt
;"Elapsed time: 2964.275 msecs" -> clojure.core/get -> clojure.lang.RT/get -> .valAt
;"Elapsed time: 2438.005 msecs" -> .valAt

;(defn- vector1d [width height xyfn]
;  (mapv (fn [i]
;          (let [x (mod i width)
;                y (int (/ i width)) ]
;            (xyfn [x y])))
;        (range (* width height))))
;
;(let [width 100,height 100
;      g2d (vector2d width height identity)
;      g1d (vector1d width height identity)
;      x 10,y 13
;      n 1e7]
;  (time (dotimes [_ n] (-> g2d (nth x nil) (nth y nil)))) ; 439ms
;  (time (dotimes [_ n] (nth g1d (+ x (* width y)))))      ; 587ms
;  )

(defn assoc-ks [m ks v]
  (if (empty? ks)
    m
    (apply assoc m (interleave ks (repeat v)))))

(defn get-cells [g2d int-positions]
  (into [] (keep g2d) int-positions))
