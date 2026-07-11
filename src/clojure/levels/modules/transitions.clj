(ns clojure.levels.modules.transitions
  (:require [moon.position :as position]))

(let [idxvalues-order [[1 0] [-1 0] [0 1] [0 -1]]]
  (assert (= (position/get-4-neighbours [0 0])
             idxvalues-order))

  ; so the idxvalues-order corresponds to the following values for a neighbour tile:

  (comment
   ; Values for every neighbour:
   {          [0 1] 1
    [-1 0]  8          [1 0] 2
              [0 -1] 4 })

  (let [idxvalues [2 8 1 4]]
    (defn idx-value [position position->transition?]
      (->> position
           position/get-4-neighbours
           (map-indexed (fn [idx position]
                          (if (position->transition? position)
                            (idxvalues idx)
                            0)))
           (apply +)))))
