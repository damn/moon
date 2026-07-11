(ns clojure.vectorgrid
  (:require [moon.g2d :as g2d]
            [clojure.g2d.height :as height]
            [clojure.g2d.width :as width]))

(deftype VectorGrid [data]
  height/Height
  (->height [_]
    (count (data 0)))

  width/Width
  (->width [_]
    (count data))

  g2d/Cells
  (cells [_]
    (apply concat data))

  g2d/Positions
  (posis [this]
    (for [x (range (width/->width this))
          y (range (height/->height this))]
      [x y]))

  clojure.lang.ILookup
  (valAt [this p]  ; {x 0 y 1} or [x y] is much slower
    (-> data
        ; nil because can be out of bounds
        (nth (p 0) nil)
        (nth (p 1) nil)))

  clojure.lang.IFn
  (invoke [this p] (.valAt this p))

  clojure.lang.Seqable
  (seq [this]
    (map #(vector %1 %2) (g2d/posis this) (g2d/cells this)))

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
