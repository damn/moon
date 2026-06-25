(ns math.vector2.double-ray-endpositions
  (:require [math.vector2.normal-vectors :as normal-vectors]
            [math.vector2.add :as add]
            [math.vector2.direction :as direction]
            [math.vector2.scale :as scale]))

(defn f
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]} ; wieso 0.98??
  (let [path-w (+ path-w 0.02) ;etwas gr�sser damit z.b. projektil nicht an ecken anst�sst
        v (direction/f [start-x start-y]
                       [target-y target-y])
        [normal1 normal2] (normal-vectors/f v)
        normal1 (scale/f normal1 (/ path-w 2))
        normal2 (scale/f normal2 (/ path-w 2))
        start1  (add/f [start-x  start-y]  normal1)
        start2  (add/f [start-x  start-y]  normal2)
        target1 (add/f [target-x target-y] normal1)
        target2 (add/f [target-x target-y] normal2)]
    [start1,target1,start2,target2]))
