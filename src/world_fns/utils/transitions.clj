(ns world-fns.utils.transitions
  (:require [clojure.math.position.get-4-neighbours :refer [get-4-neighbours]]))

(let [idxvalues-order [[1 0] [-1 0] [0 1] [0 -1]]]
  (assert (= (get-4-neighbours [0 0])
             idxvalues-order)))

(comment
  ; Values for every neighbour:
  {          [0 1] 1
   [-1 0]  8          [1 0] 2
             [0 -1] 4 })

; so the idxvalues-order corresponds to the following values for a neighbour tile:
(def ^:private idxvalues [2 8 1 4])

(defn- calculate-index-value [position->transition? idx position]
  (if (position->transition? position)
    (idxvalues idx)
    0))

(defn idx-value [position position->transition?]
  (->> position
       get-4-neighbours
       (map-indexed (partial calculate-index-value position->transition?))
       (apply +)))
