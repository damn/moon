(ns clojure.grid2d
  (:require [clojure.vectorgrid :as vectorgrid]))

; 2dimvector is 7x faster than a hashmap of [x y] to values
; like in rich hickey ant demo vectors of vectors:
; https://github.com/juliangamble/clojure-ants-simulation/blob/master/src/ants.clj
(defn create-grid
  [w h xyfn]
  {:pre [(>= w 1) (>= h 1)]}
  (vectorgrid/->VectorGrid
   (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                       (range h)))
         (range w))))
