(ns moon.g2d
  (:refer-clojure :exclude [get assoc]))

; 2dimvector is 7x faster than a hashmap of [x y] to values
; like in rich hickey ant demo vectors of vectors:
; https://github.com/juliangamble/clojure-ants-simulation/blob/master/src/ants.clj

(defn width [grid]
  (count grid))

(defn height [grid]
  (count (grid 0)))

(defn cells [grid]
  (apply concat grid))

(defn posis [grid]
  (for [x (range (width grid))
        y (range (height grid))]
    [x y]))

(defn get [grid p]
  (get-in grid p))

(defn assoc [grid p v]
  (assoc-in grid p v))

(defn create
  [w h xyfn]
  {:pre [(>= w 1) (>= h 1)]}
  (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                      (range h)))
        (range w)))
