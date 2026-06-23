(ns clojure.grid2d
  (:require [grid2d.cells :as cells]
            [grid2d.posis :as posis]
            [grid2d.height :as height]
            [grid2d.width :as width]))

; 2dimvector is 7x faster than a hashmap of [x y] to values
; like in rich hickey ant demo vectors of vectors:
; https://github.com/juliangamble/clojure-ants-simulation/blob/master/src/ants.clj

(deftype VectorGrid [data]
  height/Height
  (->height [_]
    (count (data 0)))

  width/Width
  (->width [_]
    (count data))

  cells/Cells
  (->cells [_]
    (apply concat data))

  posis/Positions
  (f [this]
    (for [x (range (width/->width this))
          y (range (height/->height this))]
      [x y]))

  clojure.lang.ILookup
  (valAt [this p]  ; {x 0 y 1} or [x y] is much slower
    (-> data
        (nth (nth p 0) nil)
        (nth (nth p 1) nil)))

  clojure.lang.IFn
  (invoke [this p] (.valAt this p))

  clojure.lang.Seqable
  (seq [this]
    (map #(vector %1 %2) (posis/f this) (cells/->cells this)))

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
    (str "width " (width/->width this) ", height " (height/->height this))))

(defn create-grid
  [w h xyfn]
  {:pre [(>= w 1) (>= h 1)]}
  (VectorGrid. (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                                   (range h)))
                     (range w))))
