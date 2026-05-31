(ns moon.position)

; not using `for` because creates a lazy seq (slow)
(let [offsets [[-1 -1] [-1 0] [-1 1] [0 -1] [0 1] [1 -1] [1 0] [1 1]]]
  (defn get-8-neighbours [position]
    (mapv #(mapv + position %) offsets)))

(defn diagonal? [[x1 y1] [x2 y2]]
  (and (not= x1 x2)
       (not= y1 y2)))

(defn get-4-neighbours [[x y]]
  [[(inc x) y]
   [(dec x) y]
   [x (inc y)]
   [x (dec y)]])
