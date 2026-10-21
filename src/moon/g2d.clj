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

  clojure.lang.Associative
  (assoc [this p v]
    (VectorGrid. (assoc-in data p v))))

(defn create
  [w h xyfn]
  {:pre [(>= w 1) (>= h 1)]}
  (VectorGrid.
   (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                       (range h)))
         (range w))))
